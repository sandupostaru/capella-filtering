/*******************************************************************************
 * Copyright (c) 2018 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.filtering.tools.listeners;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.polarsys.capella.core.model.helpers.listeners.CapellaModelDataListener;
import org.polarsys.capella.filtering.AssociatedFilteringCriterionSet;
import org.polarsys.capella.filtering.FilteringCriterion;
import org.polarsys.capella.filtering.FilteringModel;
import org.polarsys.capella.filtering.tools.FilteringToolsPlugin;

/**
 * 
 */
public class FilteringDataListener extends CapellaModelDataListener {
	private Map<FilteringCriterion, Collection<?>> implicitImpactCache;

	public FilteringDataListener() {
		implicitImpactCache = FilteringToolsPlugin.getImplicitImpactCache();
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (msg == null) {
			return;
		}

		// to take into account all tree notification with root
		super.notifyChanged(msg);

		switch (msg.getEventType()) {
		case Notification.REMOVE:
		case Notification.REMOVE_MANY:
			handleRemoveCase(msg.getOldValue());
			break;
		case Notification.ADD:
		case Notification.ADD_MANY:
			handleAddCase(msg.getNewValue());
			break;
		case Notification.SET:
			handleSetCase(msg.getNewValue());
			break;
		default:
			break;
		}

	}

	/**
	 * @param newValue
	 */
	private void handleSetCase(Object newValue) {
		if (newValue instanceof AssociatedFilteringCriterionSet) {
			removeFeatureFromCache((AssociatedFilteringCriterionSet) newValue);
		}
	}

	/**
	 * @param newValue
	 */
	private void handleAddCase(Object newValue) {
		if (newValue instanceof AssociatedFilteringCriterionSet) {
			removeFeatureFromCache((AssociatedFilteringCriterionSet) newValue);
		}
		if (newValue instanceof FilteringCriterion) {
			implicitImpactCache.remove(newValue);
		}
	}

	/**
	 * @param oldValue
	 */
	private void handleRemoveCase(Object oldValue) {
		if (oldValue instanceof AssociatedFilteringCriterionSet) {
			removeFeatureFromCache((AssociatedFilteringCriterionSet) oldValue);

		} else if (oldValue instanceof FilteringModel) {
			FilteringToolsPlugin.getImplicitImpactCache().clear();

		} else if (oldValue instanceof FilteringCriterion) {
			FilteringToolsPlugin.getImplicitImpactCache().remove(oldValue);

		} else if (oldValue instanceof List<?>) {
			for (Object elem : (List<?>) oldValue) {
				handleRemoveCase(elem);
			}
		}
	}

	/**
	 * @param value
	 */
	private void removeFeatureFromCache(AssociatedFilteringCriterionSet value) {
		List<FilteringCriterion> features = value.getFilteringCriteria();
		for (FilteringCriterion feature : features) {
			FilteringToolsPlugin.getImplicitImpactCache().remove(feature);
		}
	}
}