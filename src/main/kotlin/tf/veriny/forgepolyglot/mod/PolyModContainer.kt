/**
 * This file is part of ForgePolyglot.
 *
 * ForgePolyglot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForgePolyglot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForgePolyglot.  If not, see <http://www.gnu.org/licenses/>.
 */
package tf.veriny.forgepolyglot.mod

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import net.minecraftforge.fml.common.LoadController
import net.minecraftforge.fml.common.MetadataCollection
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.common.event.FMLEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.versioning.ArtifactVersion
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion
import net.minecraftforge.fml.common.versioning.VersionRange
import java.io.File
import java.net.URL
import java.security.cert.Certificate

class PolyModContainer(val meta: ModMetadata, val executor: Executor) : ModContainer {
    val mod = PolyModWrapper(executor)

    override fun getOwnedPackages(): MutableList<String> {
        return mutableListOf()
    }

    override fun setClassVersion(classVersion: Int) {

    }

    override fun getGuiClassName(): String? {
        return null
    }

    /**
     * A human readable name
     */
    override fun getName(): String {
        return this.meta.name
    }

    /**
     * The location on the file system which this mod came from
     */
    override fun getSource(): File? {
        return this.executor.modDirectory
    }

    override fun getDisplayVersion(): String {
        return this.meta.version
    }

    override fun getUpdateUrl(): URL? {
        if (this.meta.updateJSON == "") return null
        return URL(this.meta.updateJSON)
    }

    override fun canBeDisabled(): ModContainer.Disableable {
        return ModContainer.Disableable.NEVER
    }

    /**
     * The metadata for this mod
     */
    override fun getMetadata(): ModMetadata {
        return this.meta
    }

    override fun getSigningCertificate(): Certificate? {
        return null
    }

    /**
     * A human readable version identifier
     */
    override fun getVersion(): String {
        return this.meta.version
    }

    /**
     * Attach this mod to it's metadata from the supplied metadata collection
     */
    override fun bindMetadata(mc: MetadataCollection?) {

    }

    /**
     * Does this mod match the supplied mod
     *
     * @param mod
     */
    override fun matches(mod: Any?): Boolean {
        return false
    }

    override fun getClassVersion(): Int {
        return 1
    }

    override fun getSharedModDescriptor(): MutableMap<String, String> {
        // todo
        return mutableMapOf()
    }

    override fun acceptableMinecraftVersionRange(): VersionRange {
        return VersionRange.createFromVersionSpec("1.12.2")
    }

    override fun getCustomResourcePackClass(): Class<*>? {
        return null
    }

    /**
     * Register the event bus for the mod and the controller for error handling
     * Returns if this bus was successfully registered - disabled mods and other
     * mods that don't need real events should return false and avoid further
     * processing
     *
     * @param bus
     * @param controller
     */
    override fun registerBus(bus: EventBus, controller: LoadController): Boolean {
        bus.register(this)
        return true
    }

    /**
     * A representative string encapsulating the sorting preferences for this
     * mod
     */
    override fun getSortingRules(): String {
        return ""
    }

    override fun shouldLoadInEnvironment(): Boolean {
        return true
    }

    /**
     * Get the actual mod object
     */
    override fun getMod(): Any {
        return this.mod
    }

    override fun isImmutable(): Boolean {
        return true
    }

    override fun getCustomModProperties(): MutableMap<String, String> {
        return this.executor.properties.toMutableMap() as MutableMap<String, String>
    }

    /**
     * Set the enabled/disabled state of this mod
     */
    override fun setEnabledState(enabled: Boolean) {

    }

    /**
     * A list of the modids that this mod requires loaded prior to loading
     */
    override fun getRequirements(): MutableSet<ArtifactVersion> {
        // todo
        return mutableSetOf()
    }

    /**
     * A list of modids that should be loaded prior to this one. The special
     * value ***** indicates to load *after* any other mod.
     */
    override fun getDependencies(): MutableList<ArtifactVersion> {
        return mutableListOf()
    }

    /**
     * The globally unique modid for this mod
     */
    override fun getModId(): String {
        return this.executor.modId
    }

    /**
     * A list of modids that should be loaded *after* this one. The
     * special value ***** indicates to load *before* any
     * other mod.
     */
    override fun getDependants(): MutableList<ArtifactVersion> {
        return mutableListOf()
    }

    override fun getProcessedVersion(): ArtifactVersion {
        return DefaultArtifactVersion("0.1.0")
    }

    // non-boilerplate
    @Subscribe
    fun redispatchEvents(event: FMLEvent) {
        val typ = event.eventType
        try {
            val meth = when (event) {
                is FMLPreInitializationEvent -> this.mod::preInit
                is FMLInitializationEvent -> this.mod::init
                is FMLPostInitializationEvent -> this.mod::postInit
                else -> throw RuntimeException("Fuck")
            }
            meth.invoke(event)
        } catch (e: RuntimeException) {}

    }

    public override fun toString(): String {
        return "PolyMod: ${this.modId}"
    }

}