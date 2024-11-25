package com.uhuy.noctura.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.loadFragment(
    fragment: Fragment,
    containerId: Int,
    addToBackStack: Boolean = false,
    enterAnim: Int? = null,
    exitAnim: Int? = null
) {
    val transaction = supportFragmentManager.beginTransaction()

    // Add animations if provided
    if (enterAnim != null && exitAnim != null) {
        transaction.setCustomAnimations(enterAnim, exitAnim)
    }

    // Replace the fragment
    transaction.replace(containerId, fragment)

    // Optionally add to backstack
    if (addToBackStack) {
        transaction.addToBackStack(fragment::class.java.simpleName)
    }

    transaction.commit()
}
