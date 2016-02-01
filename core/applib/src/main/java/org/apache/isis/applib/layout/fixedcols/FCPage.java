/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.applib.layout.fixedcols;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Maps;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.layout.common.ActionLayoutData;
import org.apache.isis.applib.layout.common.ActionLayoutDataOwner;
import org.apache.isis.applib.layout.common.CollectionLayoutData;
import org.apache.isis.applib.layout.common.FieldSet;
import org.apache.isis.applib.layout.common.Page;
import org.apache.isis.applib.layout.common.PropertyLayoutData;
import org.apache.isis.applib.services.dto.Dto;

/**
 * Top-level page, consisting of an optional {@link FCColumn column} on the far left and another (also optional) on the
 * far right, with the middle consisting of a number of {@link FCTabGroup tabgroup}s, stacked vertically.
 */
@XmlRootElement(
        name = "page"
)
@XmlType(
        name = "page"
        , propOrder = {
                "actions"
                , "left"
                , "tabGroups"
                , "right"
        }
)
public class FCPage implements Page, Dto, ActionLayoutDataOwner, Serializable, FCColumnOwner, FCTabGroupOwner {

    private static final long serialVersionUID = 1L;

    private List<ActionLayoutData> actions;

    // no wrapper
    @XmlElementRef(type = ActionLayoutData.class, name="action", required = false)
    public List<ActionLayoutData> getActions() {
        return actions;
    }

    public void setActions(List<ActionLayoutData> actionLayoutDatas) {
        this.actions = actionLayoutDatas;
    }



    private FCColumn left;

    @XmlElement(required = false)
    public FCColumn getLeft() {
        return left;
    }

    public void setLeft(final FCColumn left) {
        this.left = left;
        left.setHint(FCColumn.Hint.LEFT);
    }



    private List<FCTabGroup> tabGroups;

    // no wrapper
    @XmlElement(name = "tabGroup", required = true)
    public List<FCTabGroup> getTabGroups() {
        return tabGroups;
    }

    public void setTabGroups(List<FCTabGroup> tabGroups) {
        this.tabGroups = tabGroups;
    }



    private FCColumn right;

    @XmlElement(required = false)
    public FCColumn getRight() {
        return right;
    }

    public void setRight(final FCColumn right) {
        this.right = right;
        right.setHint(FCColumn.Hint.RIGHT);
    }


    interface Visitor {
        void visit(final FCPage fcPage);
        void visit(final FCTabGroup fcTabGroup);
        void visit(final FCTab fcTab);
        void visit(final FCColumn fcColumn);
        void visit(final FieldSet fieldSet);
        void visit(final PropertyLayoutData propertyLayoutData);
        void visit(final CollectionLayoutData collectionLayoutData);
        void visit(final ActionLayoutData actionLayoutData);
    }

    public static class VisitorAdapter implements Visitor {
        @Override
        public void visit(final FCPage fcPage) { }
        @Override
        public void visit(final FCTabGroup fcTabGroup) { }
        @Override
        public void visit(final FCTab fcTab) { }
        @Override
        public void visit(final FCColumn fcColumn) { }
        @Override
        public void visit(final FieldSet fieldSet) {}
        @Override
        public void visit(final PropertyLayoutData propertyLayoutData) {}
        @Override
        public void visit(final CollectionLayoutData collectionLayoutData) {}
        @Override
        public void visit(final ActionLayoutData actionLayoutData) { }
    }


    /**
     * Visits all elements of the graph.  The {@link Visitor} implementation
     * can assume that all "owner" references are populated.
     */
    public void visit(final FCPage.Visitor visitor) {
        visitor.visit(this);
        traverseActions(this, visitor);
        traverseColumn(getLeft(), this, visitor);
        final List<FCTabGroup> tabGroups = getTabGroups();
        for (final FCTabGroup fcTabGroup : tabGroups) {
            fcTabGroup.setOwner(this);
            visitor.visit(fcTabGroup);
            final List<FCTab> tabs = fcTabGroup.getTabs();
            for (final FCTab fcTab : tabs) {
                fcTab.setOwner(fcTabGroup);
                visitor.visit(fcTab);
                traverseColumn(fcTab.getLeft(), fcTab, visitor);
                traverseColumn(fcTab.getMiddle(), fcTab, visitor);
                traverseColumn(fcTab.getRight(), fcTab, visitor);
            }
        }
        traverseColumn(getRight(), this, visitor);
    }

    private void traverseColumn(
            final FCColumn fcColumn, final FCColumnOwner fcColumnOwner, final Visitor visitor) {
        if(fcColumn == null) {
            return;
        }
        fcColumn.setOwner(fcColumnOwner);
        visitor.visit(fcColumn);
        traverseFieldSets(fcColumn, visitor);
        traverseCollections(fcColumn, visitor);
    }

    private void traverseFieldSets(final FCColumn fcColumn, final Visitor visitor) {
        for (final FieldSet fieldSet : fcColumn.getFieldSets()) {
            fieldSet.setOwner(fcColumn);
            visitor.visit(fieldSet);
            traverseActions(fieldSet, visitor);
            final List<PropertyLayoutData> properties = fieldSet.getProperties();
            for (final PropertyLayoutData propertyLayoutData : properties) {
                propertyLayoutData.setOwner(fieldSet);
                visitor.visit(propertyLayoutData);
                traverseActions(propertyLayoutData, visitor);
            }
        }
    }

    private void traverseCollections(final FCColumn fcColumn, final Visitor visitor) {
        for (final CollectionLayoutData collectionLayoutData : fcColumn.getCollections()) {
            collectionLayoutData.setOwner(fcColumn);
            visitor.visit(collectionLayoutData);
            traverseActions(collectionLayoutData, visitor);
        }
    }

    private void traverseActions(final ActionLayoutDataOwner actionLayoutDataOwner, final Visitor visitor) {
        final List<ActionLayoutData> actionLayoutDatas = actionLayoutDataOwner.getActions();
        if(actionLayoutDatas == null) {
            return;
        }
        for (final ActionLayoutData actionLayoutData : actionLayoutDatas) {
            actionLayoutData.setOwner(actionLayoutDataOwner);
            visitor.visit(actionLayoutData);
        }
    }


    @Programmatic
    @XmlTransient
    public LinkedHashMap<String, PropertyLayoutData> getAllPropertiesById() {
        final LinkedHashMap<String, PropertyLayoutData> propertiesById = Maps.newLinkedHashMap();
        visit(new FCPage.VisitorAdapter() {
            public void visit(final PropertyLayoutData propertyLayoutData) {
                propertiesById.put(propertyLayoutData.getId(), propertyLayoutData);
            }
        });
        return propertiesById;
    }


    @Programmatic
    @XmlTransient
    public LinkedHashMap<String, CollectionLayoutData> getAllCollectionsById() {
        final LinkedHashMap<String, CollectionLayoutData> collectionsById = Maps.newLinkedHashMap();

        visit(new FCPage.VisitorAdapter() {
            @Override
            public void visit(final CollectionLayoutData collectionLayoutData) {
                collectionsById.put(collectionLayoutData.getId(), collectionLayoutData);
            }
        });
        return collectionsById;
    }


    @Programmatic
    @XmlTransient
    public LinkedHashMap<String, ActionLayoutData> getAllActionsById() {
        final LinkedHashMap<String, ActionLayoutData> actionsById = Maps.newLinkedHashMap();

        visit(new FCPage.VisitorAdapter() {
            @Override
            public void visit(final ActionLayoutData actionLayoutData) {
                actionsById.put(actionLayoutData.getId(), actionLayoutData);
            }
        });
        return actionsById;
    }


    @Programmatic
    @XmlTransient
    public LinkedHashMap<String, FieldSet> getAllFieldSetsByName() {
        final LinkedHashMap<String, FieldSet> fieldSetsByName = Maps.newLinkedHashMap();

        visit(new FCPage.VisitorAdapter() {
            @Override
            public void visit(final FieldSet fieldSet) {
                fieldSetsByName.put(fieldSet.getName(), fieldSet);
            }
        });
        return fieldSetsByName;
    }


    @Programmatic
    @XmlTransient
    public LinkedHashMap<String, FCTab> getAllTabsByName() {
        final LinkedHashMap<String, FCTab> tabsByName = Maps.newLinkedHashMap();

        visit(new FCPage.VisitorAdapter() {
            @Override
            public void visit(final FCTab fcTab) {
                tabsByName.put(fcTab.getName(), fcTab);
            }
        });
        return tabsByName;
    }



    private boolean normalized;

    @Programmatic
    @XmlTransient
    public boolean isNormalized() {
        return normalized;
    }

    @Programmatic
    public void setNormalized(final boolean normalized) {
        this.normalized = normalized;
    }


}