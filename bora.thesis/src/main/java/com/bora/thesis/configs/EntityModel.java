package com.bora.thesis.configs;

import java.io.Serializable;

/**
 * common interface for entity classes. <br/>
 * each implementing class should also annotate:<br/>
 * <b>Entity</b><br/>
 * <b>Table</b>(name = "{TABLE-NAME}")<br/>
 *
 * @author bora
 */
public interface EntityModel extends Serializable {
}
