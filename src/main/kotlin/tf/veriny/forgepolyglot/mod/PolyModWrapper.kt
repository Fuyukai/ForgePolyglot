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

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager

/**
 * Wraps a polymod.
 */
class PolyModWrapper(val executor: Executor) {
    val logger = LogManager.getLogger("PolyMod-${this.executor.modId}")

    fun preInit(event: FMLPreInitializationEvent) {
        this.logger.info("Delegating PreInit...")
        this.executor.maybeExecuteFunction("pre_init", event)
    }

    fun init(event: FMLInitializationEvent) {
        this.logger.info("Delegating Init...")
        this.executor.maybeExecuteFunction("init", event)
    }

    fun postInit(event: FMLPostInitializationEvent) {
        this.logger.info("Delegating postInit...")
        this.executor.maybeExecuteFunction("post_init", event)
    }
}