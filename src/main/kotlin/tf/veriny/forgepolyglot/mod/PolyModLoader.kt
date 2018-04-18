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

import net.minecraftforge.fml.common.MetadataCollection
import net.minecraftforge.fml.common.ModContainer
import tf.veriny.forgepolyglot.util.Logging
import java.io.File
import java.io.FileInputStream

/**
 * The modloader for polyglot mods.
 */
object PolyModLoader {
    val executorMapping = mutableMapOf<String, Executor>()

    /**
     * Loads a Polymod.
     */
    fun loadMod(mods: MutableList<ModContainer>, modFile: File) {
        val executor = Executor(modFile)

        Logging.info { "Attempting to build psuedo-Mod from polymod ${executor.modId}" }
        val mcinfo = File(modFile.absolutePath + "/mcmod.info")
        val metadata = MetadataCollection.from(FileInputStream(mcinfo), modFile.absolutePath)

        // build our ModContainer
        val mcmeta = metadata.getMetadataForId(executor.modId, null)  // hehe
        val container = PolyModContainer(mcmeta, executor)
        mods.add(container)
        Logging.info { "Successfully built psuedo-Mod and injected it into FML." }
    }
}