/*
 *  Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.google.androidstudiopoet.utils

import com.google.androidstudiopoet.DependencyValidator
import com.google.androidstudiopoet.models.ProjectBlueprint
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class DependencyImageGenerator {
    companion object{
        const val CELL_SIZE = 3
        const val LINE_WIDTH = 1
        const val HEADER_SIZE = CELL_SIZE + 2 * LINE_WIDTH
        const val GRID_SIZE = CELL_SIZE + LINE_WIDTH
        val BACKGROUND_COLOR = Color(0xFFFFFF)
        val GRID_COLOR = Color(0xAFAFAF)
        val APP_COLOR = Color(0x5050FF)
        val ANDROID_COLOR = Color(0xA4C639)
        val JAVA_COLOR = Color(0xF7DB64)
        val DEPENDENCY_COLOR = Color(0x00AF00)
        val ERROR_COLOR = Color(0xAF0000)
    }

    /**
     * generates an image that represents the dependency matrix of the project
     */
    fun generate(blueprint: ProjectBlueprint) {
        val numModules = blueprint.moduleBlueprints.size + blueprint.androidModuleBlueprints.size
        val imageSize = HEADER_SIZE + LINE_WIDTH + numModules * GRID_SIZE

        // Create buffered image object
        val img = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB)
        val graphics = img.graphics

        // Background
        graphics.color = BACKGROUND_COLOR
        graphics.fillRect(0, 0, imageSize, imageSize)

        // Generate the grid
        graphics.color = GRID_COLOR
        for (i in 0..numModules) {
            // Horizontal
            val y = HEADER_SIZE + i * GRID_SIZE
            graphics.fillRect(HEADER_SIZE, y, imageSize - HEADER_SIZE, LINE_WIDTH)
            // Vertical
            val x = HEADER_SIZE + i * GRID_SIZE
            graphics.fillRect(x, HEADER_SIZE, LINE_WIDTH, imageSize - HEADER_SIZE)
        }

        // Add Android modules headers
        val numAndroid = blueprint.androidModuleBlueprints.size
        for ((index, module) in blueprint.androidModuleBlueprints.withIndex()) {
            if (module.hasLaunchActivity) {
                graphics.drawHeader(index, APP_COLOR)
            }
            else {
                graphics.drawHeader(index, ANDROID_COLOR)
            }

            // Dependencies
            for (dependency in module.moduleDependencies) {
                val split = DependencyValidator.ModuleSplit(dependency.name)
                val dependencyIndex = if (split.type == "module") {
                    // Java module
                    numAndroid + split.index
                } else {
                    split.index
                }
                graphics.drawCell(index, dependencyIndex, DEPENDENCY_COLOR)
            }
        }

        // Now Java modules
        for ((index, module) in blueprint.moduleBlueprints.withIndex()) {
            graphics.drawHeader(index + numAndroid, JAVA_COLOR)
            // Dependencies
            for (dependency in module.moduleDependencies) {
                val split = DependencyValidator.ModuleSplit(dependency.name)
                val dependencyIndex: Int
                val dependencyColor: Color
                if (split.type == "module") {
                    // Java module
                    dependencyIndex = numAndroid + split.index
                    dependencyColor = DEPENDENCY_COLOR
                } else {
                    dependencyIndex = split.index
                    dependencyColor = ERROR_COLOR
                }
                graphics.drawCell(index + numAndroid, dependencyIndex, dependencyColor)
            }
        }

        // Save to file
        val imgPath = blueprint.projectRoot.joinPath("dependencies.png")
        ImageIO.write(img,"png", File(imgPath))
        println("Dependency matrix image saved at $imgPath")
    }

    private fun Graphics.drawCell(r: Int, c: Int, color: Color) {
        val x = HEADER_SIZE + LINE_WIDTH + c * GRID_SIZE
        val y = HEADER_SIZE + LINE_WIDTH + r * GRID_SIZE
        this.color = color
        this.fillRect(x, y , CELL_SIZE, CELL_SIZE)
    }

    private fun Graphics.drawHeader(i: Int, color: Color) {
        val y = HEADER_SIZE + LINE_WIDTH + GRID_SIZE * (i)
        this.color = color
        this.fillRect(LINE_WIDTH, y, CELL_SIZE, CELL_SIZE)
        this.fillRect(y, LINE_WIDTH, CELL_SIZE, CELL_SIZE)
    }

}

