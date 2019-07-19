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
package org.komodo.relational.model.internal;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.komodo.core.internal.repository.Repository;
import org.komodo.core.repository.Descriptor;
import org.komodo.core.repository.KomodoObject;
import org.komodo.core.repository.ObjectImpl;
import org.komodo.core.repository.PropertyDescriptor;
import org.komodo.core.repository.PropertyValueType;
import org.komodo.relational.Messages;
import org.komodo.relational.internal.RelationalChildRestrictedObject;
import org.komodo.relational.internal.TypeResolver;
import org.komodo.spi.KException;
import org.komodo.spi.repository.KomodoType;
import org.komodo.spi.repository.UnitOfWork;
import org.komodo.utils.ArgCheck;
import org.komodo.utils.StringUtils;
import org.teiid.modeshape.sequencer.ddl.StandardDdlLexicon;

/**
 * An implementation of a relational model DDL statement option.
 */
public final class StatementOptionImpl extends RelationalChildRestrictedObject implements StatementOption {
	
    /**
     * The resolver of a {@link StatementOption}.
     */
    public static final TypeResolver< StatementOptionImpl > RESOLVER = new TypeResolver< StatementOptionImpl >() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.internal.TypeResolver#identifier()
         */
        @Override
        public KomodoType identifier() {
            return IDENTIFIER;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.internal.TypeResolver#owningClass()
         */
        @Override
        public Class< StatementOptionImpl > owningClass() {
            return StatementOptionImpl.class;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.internal.TypeResolver#resolvable(org.komodo.spi.repository.Repository.UnitOfWork,
         *      org.komodo.core.repository.KomodoObject)
         */
        @Override
        public boolean resolvable( final KomodoObject kobject ) throws KException {
            return ObjectImpl.validateType( kobject,
                                            StandardDdlLexicon.TYPE_STATEMENT_OPTION );
        }

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.internal.TypeResolver#resolve(org.komodo.spi.repository.Repository.UnitOfWork,
         *      org.komodo.core.repository.KomodoObject)
         */
        @Override
        public StatementOptionImpl resolve( final KomodoObject kobject ) throws KException {
            if ( kobject.getTypeId() == StatementOption.TYPE_ID ) {
                return ( StatementOptionImpl )kobject;
            }

            return new StatementOptionImpl( kobject.getTransaction(), kobject.getRepository(), kobject.getAbsolutePath() );
        }

    };

    private PropertyDescriptor descriptor;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param repository
     *        the repository where the relational object exists (cannot be <code>null</code>)
     * @param workspacePath
     *        the workspace relative path (cannot be empty)
     * @throws KException
     *         if an error occurs or if node at specified path is not a statement option
     */
    public StatementOptionImpl( final UnitOfWork transaction,
                                final Repository repository,
                                final String workspacePath ) throws KException {
        super( transaction, repository, workspacePath );
    }

    @Override
    public InputStream getBinaryValue(UnitOfWork uow) throws KException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getBooleanValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public Boolean getBooleanValue( final UnitOfWork transaction ) throws KException {
        final String value = getOption( );
        return Boolean.parseBoolean( value );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getBooleanValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Boolean[] getBooleanValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getDateValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public Calendar getDateValue( final UnitOfWork transaction ) throws KException {
        final String value = getOption( );

        try {
            final Date date = DateFormat.getInstance().parse( value );
            final Calendar result = Calendar.getInstance();
            result.setTime( date );
            return result;
        } catch ( final Exception e ) {
            throw new KException( e );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getDateValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Calendar[] getDateValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getDescriptor(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public PropertyDescriptor getDescriptor( final UnitOfWork transaction ) throws KException {
        if ( this.descriptor == null ) {
            // find descriptor in the primary type
            final Descriptor primaryType = getParent( ).getPrimaryType( );
            final String name = getName( );

            for ( final PropertyDescriptor descriptor : primaryType.getPropertyDescriptors( ) ) {
                if ( name.equals( descriptor.getName() )) {
                    this.descriptor = descriptor;
                    break;
                }
            }
        }

        return this.descriptor;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getDoubleValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public Double getDoubleValue( final UnitOfWork transaction ) throws KException {
        final String value = getOption( );
        return Double.parseDouble( value );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getDoubleValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Double[] getDoubleValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getIntegerValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public Integer getIntegerValue( final UnitOfWork transaction ) throws KException {
        final String value = getOption( );
        return Integer.parseInt( value );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getIntegerValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Integer[] getIntegerValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getLongValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public Long getLongValue( final UnitOfWork transaction ) throws KException {
        final String value = getOption( );
        return Long.parseLong( value );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getLongValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Long[] getLongValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.relational.model.internal.StatementOption#getOption()
     */
    @Override
    public String getOption( ) throws KException {
        return getObjectProperty( getTransaction(), PropertyValueType.STRING, "getOption", StandardDdlLexicon.VALUE ); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getStringValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public String getStringValue( final UnitOfWork transaction ) throws KException {
        return getOption( );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getStringValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public String[] getStringValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.ObjectImpl#getTypeIdentifier(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public KomodoType getTypeIdentifier( ) {
        return StatementOption.IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getValue(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public String getValue( final UnitOfWork transaction ) throws KException {
        return getOption( );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#getValues(org.komodo.spi.repository.Repository.UnitOfWork)
     * @throws UnsupportedOperationException
     *         if called
     */
    @Override
    public Object[] getValues( final UnitOfWork transaction ) {
        throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns {@link PropertyValueType#STRING}
     *
     * @see org.komodo.core.repository.Property#getValueType(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public PropertyValueType getValueType( final UnitOfWork transaction ) {
        return PropertyValueType.STRING;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns <code>false</code>
     *
     * @see org.komodo.core.repository.Property#isMultiple(org.komodo.spi.repository.Repository.UnitOfWork)
     */
    @Override
    public boolean isMultiple( final UnitOfWork transaction ) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.core.repository.Property#set(org.komodo.spi.repository.Repository.UnitOfWork, java.lang.Object[])
     */
    @Override
    public void set( final UnitOfWork transaction,
                     final Object... values ) throws KException {
        if ( ( values == null ) || ( values.length == 0 ) ) {
            setOption( null );
        } else if ( values.length == 1 ) {
            setOption( values[0].toString() );
        } else {
            throw new UnsupportedOperationException( Messages.getString( Messages.Relational.INVALID_STATEMENT_OPTION_VALUE ) );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.relational.model.internal.StatementOption#setOption(java.lang.String)
     */
    @Override
    public void setOption( final String newOption ) throws KException {
        ArgCheck.isNotEmpty( newOption, "newOption" ); //$NON-NLS-1$
        setObjectProperty( getTransaction(), "setOption", StandardDdlLexicon.VALUE, newOption ); //$NON-NLS-1$

        if ( StringUtils.isBlank( newOption ) ) {
            remove( getTransaction() );
        }
    }

}