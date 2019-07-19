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
package org.komodo.relational.model;

import org.komodo.relational.RelationalObject;
import org.komodo.spi.KException;
import org.komodo.spi.repository.Exportable;
import org.komodo.spi.repository.KomodoType;

/**
 * Represents a relational model table.
 */
public interface Table extends Exportable, RelationalObject, SchemaElement {

    /**
     * The type identifier.
     */
    int TYPE_ID = Table.class.hashCode();

    /**
     * Identifier of this object
     */
    KomodoType IDENTIFIER = KomodoType.TABLE;

    /**
     * The on commit value.
     */
    public enum OnCommit {

        /**
         * Delete rows on commit.
         */
        DELETE_ROWS( "DELETE ROWS" ), //$NON-NLS-1$

        /**
         * Preserve rows on commit.
         */
        PRESERVE_ROWS( "PRESERVE ROWS" ); //$NON-NLS-1$

        /**
         * @param value
         *        the value whose <code>OnCommit</code> is being requested (can be empty)
         * @return the corresponding <code>OnCommit</code> or <code>null</code> if not found
         */
        public static OnCommit fromValue( final String value ) {
            if (DELETE_ROWS.value.equals(value)) {
                return DELETE_ROWS;
            }

            if (PRESERVE_ROWS.value.equals(value)) {
                return PRESERVE_ROWS;
            }

            return null;
        }

        private final String value;

        private OnCommit( final String value ) {
            this.value = value;
        }

        /**
         * @return the Teiid value (cannot be empty)
         */
        public String toValue() {
            return this.value;
        }

    }

    /**
     * Temporary table types.
     */
    public enum TemporaryType {

        /**
         * A globally-scoped temporary table.
         */
        GLOBAL,

        /**
         * A locally-scoped temporary table.
         */
        LOCAL;

        /**
         * @param value
         *        the value whose <code>TemporaryType</code> is being requested (can be empty)
         * @return the corresponding <code>TemporaryType</code> or <code>null</code> if not found
         */
        public static TemporaryType fromValue( final String value ) {
            if (GLOBAL.name().equals(value)) {
                return GLOBAL;
            }

            if (LOCAL.name().equals(value)) {
                return LOCAL;
            }

            return null;
        }

    }

    /**
     * The default value of this table's cardinality. Value is {@value} .
     */
    long DEFAULT_CARDINALITY = -1;

    /**
     * The default value indicating if this table is materialized. Value is {@value} .
     */
    boolean DEFAULT_MATERIALIZED = false;

    /**
     * The default value indicating if this table is updatable. Value is {@value} .
     */
    boolean DEFAULT_UPDATABLE = true;

    @Override
    Model getRelationalParent( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param columnName
     *        the name of the column being added (cannot be empty)
     * @return the new column (never <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    Column addColumn( 
                      final String columnName ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param foreignKeyName
     *        the name of the foreign key being added (cannot be empty)
     * @param referencedTable
     *        the table referenced by this foreign key (cannot be <code>null</code>)
     * @return the new foreign key (never <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    ForeignKey addForeignKey( 
                              final String foreignKeyName,
                              final Table referencedTable ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param constraintName
     *        the name of the unique constraint being added (cannot be empty)
     * @return the new unique constraint (never <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    UniqueConstraint addUniqueConstraint( 
                                          final String constraintName ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the cardinality
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_CARDINALITY
     */
    long getCardinality( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param namePatterns
     *        optional name patterns (can be <code>null</code> or empty but cannot have <code>null</code> or empty elements)
     * @return the columns (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    Column[] getColumns( 
                         final String... namePatterns ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the value of the <code>description</code> property (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getDescription( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param namePatterns
     *        optional name patterns (can be <code>null</code> or empty but cannot have <code>null</code> or empty elements)
     * @return the foreign keys (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    ForeignKey[] getForeignKeys( 
                                 final String... namePatterns ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the materialized table name (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getMaterializedTable( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the value of the <code>name in source</code> property (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getNameInSource( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the on commit value (can be <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    OnCommit getOnCommitValue( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the primary key (can be <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    PrimaryKey getPrimaryKey( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the query expression (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getQueryExpression( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the temporary table type or <code>null</code> if not a temporary table
     * @throws KException
     *         if an error occurs
     */
    TemporaryType getTemporaryTableType( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param namePatterns
     *        optional name patterns (can be <code>null</code> or empty but cannot have <code>null</code> or empty elements)
     * @return the unique constraints (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    UniqueConstraint[] getUniqueConstraints( 
                                             final String... namePatterns ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the value of the <code>UUID</code> option (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getUuid( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return <code>true</code> if this is a materialized table
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_MATERIALIZED
     */
    boolean isMaterialized( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return <code>true</code> if this table is updatable
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_UPDATABLE
     */
    boolean isUpdatable( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param columnToRemove
     *        the name of the column being removed (cannot be empty)
     * @throws KException
     *         if an error occurs
     */
    void removeColumn( 
                       final String columnToRemove ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param foreignKeyToRemove
     *        the name of the foreign key being removed (cannot be empty)
     * @throws KException
     *         if an error occurs
     */
    void removeForeignKey( 
                           final String foreignKeyToRemove ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @throws KException
     *         if an error occurs
     */
    void removePrimaryKey( ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param constraintToRemove
     *        the name of the unique constraint being removed (cannot be empty)
     * @throws KException
     *         if an error occurs
     */
    void removeUniqueConstraint( 
                                 final String constraintToRemove ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newCardinality
     *        the new value for the <code>cardinality</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_CARDINALITY
     */
    void setCardinality( 
                         long newCardinality ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newDescription
     *        the new value of the <code>description</code> property (can only be empty when removing)
     * @throws KException
     *         if an error occurs
     */
    void setDescription( 
                         final String newDescription ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newMaterialized
     *        the new value for the <code>materialized</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_MATERIALIZED
     */
    void setMaterialized( 
                          final boolean newMaterialized ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newMaterializedTable
     *        the new value for the <code>materialized table</code> property (can only be empty when removing)
     * @throws KException
     *         if an error occurs
     */
    void setMaterializedTable( 
                               final String newMaterializedTable ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newNameInSource
     *        the new name in source (can only be empty when removing)
     * @throws KException
     *         if an error occurs
     */
    void setNameInSource( 
                          final String newNameInSource ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newOnCommit
     *        the new value for the <code>on commit value</code> property (can be <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    void setOnCommitValue( 
                           final OnCommit newOnCommit ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newPrimaryKeyName
     *        the name of the new <code>primary key</code> child (cannot be empty)
     * @return the new primary key (never <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    PrimaryKey setPrimaryKey( 
                              final String newPrimaryKeyName ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newQueryExpression
     *        the new value for the <code>query expression</code> property (can be empty)
     * @throws KException
     *         if an error occurs
     */
    void setQueryExpression( 
                             final String newQueryExpression ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newTempType
     *        the new value for the <code>temporary table type</code> property (can be <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    void setTemporaryTableType( 
                                final TemporaryType newTempType ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newUpdatable
     *        the new value for the <code>updatable</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_UPDATABLE
     */
    void setUpdatable( 
                       final boolean newUpdatable ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newUuid
     *        the new value of the <code>UUID</code> option (can only be empty when removing)
     * @throws KException
     *         if an error occurs
     */
    void setUuid( 
                  final String newUuid ) throws KException;

}