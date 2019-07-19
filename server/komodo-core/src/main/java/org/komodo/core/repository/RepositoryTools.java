/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.core.repository;

import java.io.PrintStream;

import org.komodo.core.internal.repository.Repository;
import org.komodo.spi.StringConstants;
import org.komodo.spi.repository.UnitOfWork;
import org.komodo.utils.ArgCheck;
import org.komodo.utils.StringUtils;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.JcrTools;

/**
 * Utility functions for adding remove objects from repositories
 */
public class RepositoryTools implements StringConstants {

    private RepositoryTools() {}

    /**
     * Get or create a KomodoObject at the specified path.
     * @param transaction transaction
     * @param parent the parent KomodoObject. may not be null
     * @param path the path of the desired child KomodoObject. may not be null
     * @param defaultType the default KomodoObject type. may be null
     * @param finalType the optional final KomodoObject type. may be null
     * @return the existing or newly created KomodoObject
     * @throws Exception if an error occurs
     * @throws IllegalArgumentException if either the parent or path argument is null
     */
    public static KomodoObject findOrCreate(UnitOfWork transaction,
                                  KomodoObject parent,
                                  String path,
                                  String defaultType,
                                  String finalType ) throws Exception {
        ArgCheck.isNotNull(parent, "parentKomodoObject"); //$NON-NLS-1$
        ArgCheck.isNotNull(path, "path"); //$NON-NLS-1$
        // Remove leading and trailing slashes ...
        String relPath = path.replaceAll("^/+", "").replaceAll("/+$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        // Look for the KomodoObject first ...
        if ( parent.hasRawChild( transaction, relPath, defaultType ) ) {
            for ( final KomodoObject kid : parent.getRawChildren( transaction, relPath ) ) {
                if ( defaultType.equals( kid.getPrimaryType( ).getName() )
                     || kid.hasDescriptor( defaultType ) ) {
                    return kid;
                }
            }
        }

        // Create the KomodoObject, which has to be done segment by segment ...
        String[] pathSegments = relPath.split("/"); //$NON-NLS-1$
        KomodoObject komodoObject = parent;
        for (int i = 0, len = pathSegments.length; i != len; ++i) {
            String pathSegment = pathSegments[i];
            pathSegment = pathSegment.trim();
            if (pathSegment.length() == 0) continue;
            if (komodoObject.hasRawChild(transaction, pathSegment)) {
                // Find the existing KomodoObject ...
                komodoObject = parent.getRawChildren( transaction, pathSegment )[ 0 ];
            } else {
                // Make sure there is no index on the final segment ...
                String pathSegmentWithNoIndex = pathSegment.replaceAll("(\\[\\d+\\])+$", ""); //$NON-NLS-1$ //$NON-NLS-2$
                // Create the KomodoObject ...
                String komodoObjectType = defaultType;
                if (i == len - 1 && finalType != null)
                    komodoObjectType = finalType;

                if (komodoObjectType != null) {
                    komodoObject = komodoObject.addChild(transaction, pathSegmentWithNoIndex, komodoObjectType);
                } else {
                    komodoObject = komodoObject.addChild(transaction, pathSegmentWithNoIndex, JcrConstants.NT_UNSTRUCTURED);
                }
            }
        }

        return komodoObject;
    }

    /**
     * Get or create a KomodoObject with the specified KomodoObject and KomodoObject type under the specified parent KomodoObject.
     * @param transaction the transaction
     * @param parent the parent KomodoObject. may not be null
     * @param name the name of the child KomodoObject. may not be null
     * @param komodoObjectType the KomodoObject type. may be null
     * @return the existing or newly created child KomodoObject
     * @throws Exception if an error occurs
     */
    public static KomodoObject findOrCreateChild(UnitOfWork transaction,
                                   KomodoObject parent,
                                   String name,
                                   String komodoObjectType ) throws Exception {
        return findOrCreate(transaction, parent, name, komodoObjectType, komodoObjectType);
    }

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> and must have a state of
     *        {@link org.komodo.spi.repository.UnitOfWork.State#NOT_STARTED})
     * @param repository
     *        the repository where the object can be found (cannot be <code>null</code>)
     * @param id
     *        the object identifier (cannot be empty)
     * @return the path of the requested object or <code>null</code> if not found
     * @throws Exception
     *         if an error occurs
     */
    public static String findPathOfReference( final UnitOfWork transaction,
                                              final Repository repository,
                                              final String id ) throws Exception {
        ArgCheck.isNotNull( transaction, "transaction" ); //$NON-NLS-1$
        ArgCheck.isTrue( ( transaction.getState() == org.komodo.spi.repository.UnitOfWork.State.NOT_STARTED ),
                         "transaction state is not NOT_STARTED" ); //$NON-NLS-1$
        ArgCheck.isNotNull( repository, "repository" ); //$NON-NLS-1$
        ArgCheck.isNotEmpty( id, "id" ); //$NON-NLS-1$

        final KomodoObject kobject = repository.getUsingId( transaction, id );

        if ( kobject == null ) {
            return null;
        }

        return kobject.getAbsolutePath();
    }

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not
     *        {@link org.komodo.spi.repository.UnitOfWork.State#NOT_STARTED})
     * @param property
     *        the property whose display value is being requested (cannot be empty)
     * @return String representation including the property name and its values
     */
    public static String getDisplayNameAndValue(UnitOfWork transaction,
                                                Property property) {
        ArgCheck.isNotNull( transaction, "transaction" ); //$NON-NLS-1$
        ArgCheck.isTrue( ( transaction.getState() == org.komodo.spi.repository.UnitOfWork.State.NOT_STARTED ),
                         "transaction state is not NOT_STARTED" ); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        try {
            sb.append(property.getName()).append('=');
            sb.append(getDisplayValue(transaction, property));
        } catch (Exception e) {
            sb.append(" on deleted node ").append(property.getAbsolutePath()); //$NON-NLS-1$
        }

        return sb.toString();
    }

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not
     *        {@link org.komodo.spi.repository.UnitOfWork.State#NOT_STARTED})
     * @param property
     *        the property whose display value is being requested (cannot be empty)
     * @return String representation of property values
     * @throws Exception the exception
     */
    public static String getDisplayValue(UnitOfWork transaction,
    		Property property) throws Exception {
        ArgCheck.isNotNull( transaction, "transaction" ); //$NON-NLS-1$
        ArgCheck.isTrue( ( transaction.getState() == org.komodo.spi.repository.UnitOfWork.State.NOT_STARTED ),
                         "transaction state is not NOT_STARTED" ); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        final PropertyValueType type = property.getDescriptor( transaction ).getType();
        final boolean propIsReference = ( ( PropertyValueType.REFERENCE == type ) );
        final boolean propIsBinary = ( PropertyValueType.BINARY == type );

    	if (property.isMultiple(transaction)) {
    		sb.append('[');
    		Object[] values = property.getValues(transaction);
    		for (int i = 0; i < values.length; ++i) {
    			Object value = values[i];

                if ( propIsReference ) {
                    final String path = findPathOfReference( transaction, property.getRepository(), value.toString() );

                    if (!StringUtils.isBlank( path )) {
                        value = path;
                    }
                }

                if (propIsBinary)
                    value = "*** binary value not shown ***";

    			sb.append(value);
    			if ((i + 1) < values.length)
    				sb.append(',');
    		}
    		sb.append(']');
    	} else {
    		Object value = property.getValue(transaction);

            if ( propIsReference ) {
                final String path = findPathOfReference( transaction, property.getRepository(), value.toString() );

                if (!StringUtils.isBlank( path )) {
                    value = path;
                }
            }

            if (propIsBinary)
                value = "*** binary value not shown ***";

            sb.append(value);
    	}

    	return sb.toString();
    }

    /**
     * Traverses the graph of the given {@link KomodoObject} and returns its
     * String representation.
     *
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not
     *        {@link org.komodo.spi.repository.UnitOfWork.State#NOT_STARTED})
     * @param kObject object to be traversed
     * @return string representation of object's graph
     * @throws Exception if error occurs
     */
    public static String traverse(UnitOfWork transaction,
                                  KomodoObject kObject) throws Exception {
        ArgCheck.isNotNull( transaction, "transaction" ); //$NON-NLS-1$
        ArgCheck.isTrue( ( transaction.getState() == org.komodo.spi.repository.UnitOfWork.State.NOT_STARTED ),
                         "transaction state is not NOT_STARTED" ); //$NON-NLS-1$

        TraversalOutputVisitor visitor = new TraversalOutputVisitor();
        return visitor.visit(transaction, kObject);
    }

    /**
     * Visitor for printing out the full tree of a {@link KomodoObject}.
     *
     * This can do the same job as {@link JcrTools#printNode(javax.jcr.Node)}, however
     * the latter only prints to System.out while this visitor returns the string allowing
     * it to be sent to alternative {@link PrintStream}s.
     *
     */
    public static class TraversalOutputVisitor implements KomodoObjectVisitor {

        private final StringBuffer buffer = new StringBuffer(NEW_LINE);

        private String createIndent(String path) {
            StringBuffer indent = new StringBuffer(TAB);
            String[] levels = path.split(FORWARD_SLASH);

            for (int i = 0; i < levels.length; ++i) {
                indent.append(TAB);
            }

            return indent.toString();
        }

        @Override
        public OperationType getRequestType() {
            return OperationType.READ_OPERATION;
        }

        @Override
        public String visit(UnitOfWork transaction,
                            KomodoObject object) throws Exception {
            String indent = createIndent(object.getAbsolutePath());
            buffer.append(indent + object.getName() + NEW_LINE);

            //
            // Avoid relational filters of object's children
            //
            ObjectImpl bareObject = new ObjectImpl(transaction, object.getRepository(), object.getAbsolutePath(), object.getIndex());
            String[] propertyNames = bareObject.getPropertyNames();

            for (String propertyName : propertyNames) {
                Property property = bareObject.getProperty(propertyName);
                buffer.append(indent + TAB + AT + getDisplayNameAndValue(transaction, property) + NEW_LINE);
            }

            KomodoObject[] children = bareObject.getChildren();
            for (int i = 0; i < children.length; ++i)
                children[i].accept(transaction, this);

            return buffer.toString();
        }
    }
}
