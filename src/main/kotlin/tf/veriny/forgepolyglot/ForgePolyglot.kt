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
package tf.veriny.forgepolyglot

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import tf.veriny.forgepolyglot.mod.Executor
import tf.veriny.forgepolyglot.util.Logging
import java.io.File

@Mod(
    modid = "forgepolyglot",
    name = "ForgePolyglot",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    version = "0.1.0",
    dependencies = "required-after:forgelin"
)
object ForgePolyglot {
    var active: Boolean = false
    val mods = mapOf<String, ModContainer>()

    init {
        // ensure we are running on GraalVM
        val prop = System.getProperty("java.vm.name")
        if (!prop.startsWith("GraalVM")) {
            System.err.println("Loaded on JVM $prop - not a GraalVM! To use ForgePolyglot, " +
                "you must load on GraalVM.")
        } else {
            this.hijackFML()
        }
    }

    /**
     * Hijacks FML, installing our own mod containers.
     */
    @JvmStatic fun hijackFML() {

    }

    // this is basically a modloader inside a modloader
    // throwback to 1.2.5
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        // don't work if we don't have GraalVM to use
        if (!this.active) {
            return
        }

        Logging.logger = event.modLog

        val rootPath = event.modConfigurationDirectory.parentFile.absolutePath
        val polyglotPath = "$rootPath/mods/polyglot"
        val polyglotDir = File(polyglotPath)
        event.modLog.info("Loading Polyglot mods from $polyglotPath")

        for (file in polyglotDir.listFiles()) {
            if (!file.isDirectory) {
                Logging.warning { "Got non-directory file ${file.name} in mod directory, ignoring" }
                continue
            }
            val executor = Executor(file)
            executor.load()
        }
    }
}