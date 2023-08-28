package com.elveum.effects.processor

import java.lang.Exception
import javax.lang.model.element.Element

/**
 * Indicates that element being processed is wrong or doesn't conform the requirements.
 */
class ElementException(
    message: String,
    val element: Element
) : Exception(message)