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
import com.bangkit.inscure.utils.Constanta

class EditTextEmail : AppCompatEditText {

    private lateinit var emailIcon: Drawable

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

        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_alternate_email_24px)!!

        // Change the icon color
        @Suppress("DEPRECATION")
        emailIcon.setColorFilter(ContextCompat.getColor(context, R.color.secondary_light), PorterDuff.Mode.SRC_IN)
        emailIcon.setBounds(0, 0, emailIcon.intrinsicWidth, emailIcon.intrinsicHeight)
        setCompoundDrawables(emailIcon, null, null, null)

        // Set padding between the icon and text
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_padding)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.isNotEmpty()) {
                    if (!s.toString().matches(Constanta.emailPattern)) {
                        context.getString(R.string.validation_invalid_email)
                    } else null
                } else null
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing
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
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}
