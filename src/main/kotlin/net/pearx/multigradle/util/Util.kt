/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util

import kotlin.reflect.KProperty

internal class Alias<RECEIVER, FINAL>(
    private val receiver: RECEIVER,
    private val getter: RECEIVER.() -> FINAL,
    private val setter: RECEIVER.(FINAL) -> Unit
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): FINAL = receiver.getter()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: FINAL) {
        receiver.setter(value)
    }
}

internal fun <RECEIVER, FINAL> RECEIVER.alias(
    getter: RECEIVER.() -> FINAL,
    setter: RECEIVER.(FINAL) -> Unit
) = Alias(this, getter, setter)