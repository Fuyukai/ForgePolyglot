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
package tf.veriny.forgepolyglot.barrier

import org.graalvm.polyglot.Context
import tf.veriny.forgepolyglot.barrier.wrapper.BlockWrapper

/**
 * Used to export symbols into a Polymod from Java-land.
 */
object Exporter {
    /**
     * The symbol mapping to export.
     */
    private val symbolMapping = mutableMapOf<String, Any?>()

    /**
     * Adds a symbol to the export mapping.
     */
    fun addSymbol(name: String, value: Any?) = this.symbolMapping.put(name, value)

    /**
     * Exports symbols into a polyglot context.
     */
    fun exportSymbols(context: Context) {
        val binding = context.polyglotBindings
        this.symbolMapping.entries.forEach { binding.putMember(it.key, it.value) }
    }

}