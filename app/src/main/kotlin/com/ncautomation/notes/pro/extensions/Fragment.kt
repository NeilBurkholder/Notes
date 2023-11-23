package com.ncautomation.notes.pro.extensions

import androidx.fragment.app.Fragment
import com.ncautomation.notes.pro.helpers.Config

val Fragment.config: Config? get() = if (context != null) Config.newInstance(context!!) else null
