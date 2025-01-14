package gg.mineral.practice.inventory

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ClickCancelled(val value: Boolean)
