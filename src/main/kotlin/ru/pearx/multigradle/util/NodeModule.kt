package ru.pearx.multigradle.util

data class NodeModule(val name: String, val version: String)
{
    override fun toString(): String = "$name@$version"
}