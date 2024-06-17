package com.bangkit.inscure.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.inscure.R

class EditTextPassword : AppCompatEditText {

    private var charLength = 0
    private lateinit var passwordIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        passwordIcon = ContextCompat.getDrawable(context, R.drawable.ic_password_baseline_24)!!

        @Suppress("DEPRECATION")
        passwordIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_dark), PorterDuff.Mode.SRC_IN)
        passwordIcon.setBounds(0, 0, passwordIcon.intrinsicWidth, passwordIcon.intrinsicHeight)
        setCompoundDrawables(passwordIcon, null, null, null)
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_padding)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                charLength = s.length
                error = if (charLength <= 7) context.getString(R.string.validation_password_rules) else null
            }

            override fun afterTextChanged(edt: Editable?) {
                // do nothing
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            background = ContextCompat.getDrawable(this, R.drawable.custom_form_input)
            setTextColor(ContextCompat.getColor(this, R.color.primary_dark))
            setHintTextColor(ContextCompat.getColor(this, R.color.secondary_light))
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}
