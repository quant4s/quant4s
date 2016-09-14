package com.bostontechnologies.quickfixs.fields

import quickfix.StringField

class MDStreamID(data: String = null) extends StringField(MDStreamID.FIELD, data)

object MDStreamID {

	val FIELD = 1500

}

