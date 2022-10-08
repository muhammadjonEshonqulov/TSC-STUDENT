package uz.jbnuu.tsc.student.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

/**
 *  Safely navigate via the given Action
 *
 *  A fix for [Navigation action cannot be found in the current destination] crash
 * @param resActionId action that describes this navigation operation
 *
 * @author @BoyWonder
 */
fun NavController.navigateSafe(
    @IdRes resActionId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    extras: Navigator.Extras? = null
) {
    val destinationId = currentDestination?.getAction(resActionId)?.destinationId
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != null) {
            currentNode?.findNode(destinationId)
                ?.let { navigate(resActionId, args, navOptions, extras) }
        }
    }
}

/**
 *  Safely navigate via the given Action
 *
 *  A fix for [Navigation action cannot be found in the current destination] crash
 * @param resActionId action that describes this navigation operation
 * @param args arguments to pass to the destination
 *
 * @author @BoyWonder
 */
fun NavController.navigateSafe(@IdRes resActionId: Int, args: Bundle?) {
    val destinationId = currentDestination?.getAction(resActionId)?.destinationId
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != null) {
            currentNode?.findNode(destinationId)
                ?.let { navigate(resActionId, args) }
        }
    }
}