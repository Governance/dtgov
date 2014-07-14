package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Enum with the different process status values.
 * 
 * @author David Virgil Naranjo
 */
@Portable
public enum ProcessStatusEnum implements Serializable {
    RUNNING, ABORTED, COMPLETED
}
