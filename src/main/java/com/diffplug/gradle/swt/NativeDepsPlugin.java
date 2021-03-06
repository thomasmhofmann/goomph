/*
 * Copyright 2016 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.swt;

import java.util.List;
import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

import com.diffplug.common.collect.ImmutableList;
import com.diffplug.common.swt.os.SwtPlatform;
import com.diffplug.gradle.ProjectPlugin;
import com.diffplug.gradle.p2.AsMavenPlugin;
import com.diffplug.gradle.pde.EclipseRelease;

/**
 * Adds the platform-specific SWT and jface jars which are appropriate for the
 * currently running platform (on the dev machine).
 * 
 * Adds the following jars:
 * 
 * * `org.eclipse.swt`
 * * `org.eclipse.jface`
 * * `org.eclipse.core.commands`
 * * `org.eclipse.equinox.common`
 * 
 * ```groovy
 * apply plugin: 'com.diffplug.gradle.swt.nativedeps'
 * ```
 * 
 * * Property `SWT_VERSION` sets the eclipse version from which to get SWT (e.g. `4.6.0`).
 * * Property `SWT_P2_REPO` sets the p2 repository which is being used (ignores the SWT_VERSION property).
 * * Property `SWT_P2_GROUP` sets the maven group name for the downloaded artifacts, (defaults to `eclipse-swt-deps`).
 *
 * ### Example projects
 * 
 * * [durian-swt](https://github.com/diffplug/durian-swt)
 * * (send us yours in a [PR](https://github.com/diffplug/goomph)!)
 * 
 */
public class NativeDepsPlugin extends ProjectPlugin {
	static final String PROP_VERSION = "SWT_VERSION";
	static final String PROP_REPO = "SWT_P2_REPO";
	static final String PROP_GROUP = "SWT_P2_GROUP";

	static final String DEFAULT_GROUP = "eclipse-swt-deps";

	static String getGroup(Project project) {
		String group = (String) project.getProperties().get(PROP_GROUP);
		return Optional.ofNullable(group).orElse(DEFAULT_GROUP);
	}

	static String getRepo(Project project) {
		String repo = (String) project.getProperties().get(PROP_REPO);
		String version = (String) project.getProperties().get(PROP_VERSION);
		if (repo != null) {
			return repo;
		} else if (version != null) {
			return EclipseRelease.official(version).updateSite();
		} else {
			return EclipseRelease.latestOfficial().updateSite();
		}
	}

	@Override
	protected void applyOnce(Project project) {
		String swtGroup = getGroup(project);

		// add the p2 repo and its dependencies
		AsMavenPlugin asMavenPlugin = ProjectPlugin.getPlugin(project, AsMavenPlugin.class);
		asMavenPlugin.extension().group(swtGroup, group -> {
			group.repo(getRepo(project));
			DEPS.forEach(group::iu);
		});

		// add all of SWT's dependencies 
		ProjectPlugin.getPlugin(project, JavaPlugin.class);
		for (String dep : DEPS) {
			project.getDependencies().add("compile", swtGroup + ":" + dep + ":+");
		}
		project.getDependencies().add("compile", swtGroup + ":" + SWT + "." + SwtPlatform.getRunning() + ":+");
	}

	static final String SWT = "org.eclipse.swt";
	static final String JFACE = "org.eclipse.jface";
	static final String CORE_COMMANDS = "org.eclipse.core.commands";
	static final String EQUINOX_COMMON = "org.eclipse.equinox.common";

	static final List<String> DEPS = ImmutableList.of(SWT, JFACE, CORE_COMMANDS, EQUINOX_COMMON);
}
