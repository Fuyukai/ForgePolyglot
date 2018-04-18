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

import com.google.common.collect.ImmutableList
import com.google.common.collect.Maps
import net.minecraftforge.fml.common.LoadController
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.event.FMLLoadEvent
import tf.veriny.forgepolyglot.mod.PolyModLoader
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
    init {
        // ensure we are running on GraalVM
        val prop = System.getProperty("java.vm.name")
        if (!prop.startsWith("GraalVM")) {
            System.err.println("Loaded on JVM $prop - not a GraalVM! To use ForgePolyglot, " +
                "you must load on GraalVM.")

        } else {
            // fuck with FML
            this.load()
        }
    }

    /**
     * Called to load this mod, and load Polymods.
     */
    fun load() {
        // todo: open an issue allowing us to directly meddle with fml mods more easily


        val loader = Loader.instance()
        val modsField = loader::class.java.getDeclaredField("mods")
        Logging.info { "Meddling with FML to let us edit mods..." }
        // allow us to meddle with mods
        modsField.isAccessible = true
        // make it mutable
        val old = modsField.get(loader) as List<ModContainer>
        val mods = mutableListOf<ModContainer>()
        mods.addAll(old)

        // scan for our mods
        // question: how to get the mods directory?
        // answer:
        val polyModPath = File(loader.configDir.parentFile.absolutePath + "/mods/polymods")

        // make the mod path, if it doesn't exist
        if (!polyModPath.exists()) polyModPath.mkdir()

        for (file in polyModPath.listFiles()) {
            // ignore files, we need directories
            if (!file.isDirectory) continue
            PolyModLoader.loadMod(mods, file)
        }
        // unfuck mod list
        Logging.info { "Meddling with FML to fix mods..." }
        modsField.set(loader, mods.toMutableList())
        // unfuck namedMods
        Logging.info { "Meddling with FML to fix named mods..." }
        val namedModsF = loader::class.java.getDeclaredField("namedMods")
        namedModsF.isAccessible = true
        namedModsF.set(loader, Maps.uniqueIndex(mods, { it: ModContainer? -> it?.name }))
        // unwrap modController
        Logging.info { "Meddling with FML to update active mods..." }
        val modControllerF = loader::class.java.getDeclaredField("modController")
        modControllerF.isAccessible = true
        val modController = modControllerF.get(loader) as LoadController
        modController.buildModList(FMLLoadEvent())

        Logging.info { "Meddling with FML to fix mods for the last time..." }
        val sortMeth = loader::class.java.getDeclaredMethod("sortModList")
        sortMeth.isAccessible = true
        sortMeth.invoke(loader)
        modsField.set(loader, ImmutableList.copyOf(modsField.get(loader) as List<ModContainer>))
    }
}