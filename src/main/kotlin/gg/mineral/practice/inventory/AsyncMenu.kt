package gg.mineral.practice.inventory

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

abstract class AsyncMenu : PracticeMenu() {

    private fun updateAsync(): CompletableFuture<Void> = CompletableFuture.runAsync {
        try {
            update()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun open(viewer: Profile, pageNumber: Int) {

        val hadUpdate = if (needsUpdate || shouldUpdate()) {
            updateAsync().thenApply {
                needsUpdate = false
                true
            }
        } else CompletableFuture.completedFuture(false)

        hadUpdate.thenAccept {
            Bukkit.getScheduler().runTask(PracticePlugin.INSTANCE) {
                this.closed = false
                this.viewer = viewer

                val page = pageMap.computeIfAbsent(
                    pageNumber,
                    Int2ObjectFunction { num -> Page(num) })

                page.open(viewer, it)

                openPage = page

                viewer.openMenu = this
            }
        }
    }
}