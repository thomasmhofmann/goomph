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
package com.diffplug.gradle.oomph;

import com.diffplug.common.swt.os.OS;

public class ConventionStyle extends OomphConvention {
	ConventionStyle(OomphIdeExtension extension) {
		super(extension);
		extension.getP2().addIU(IUs.IDE);
	}

	/** Sets nice font and whitespace settings (Consolas/Monaco/Monospace). */
	public void niceText() {
		niceText(OS.getNative().winMacLinux("9.0", "11.0", "10.0"));
	}

	/** Sets nice font and whitespace settings. */
	public void niceText(String fontSize) {
		// improved fonts
		String font = OS.getNative().winMacLinux("Consolas", "Monaco", "Monospace");
		extension.workspaceProp(".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.workbench.prefs", props -> {
			props.put("eclipse.preferences.version", "1");
			props.put("org.eclipse.jface.textfont", "1|" + font + "|" + fontSize + "|0|WINDOWS|1|-12|0|0|0|400|0|0|0|0|3|2|1|49|" + font);
		});
		// visible whitespace
		extension.workspaceProp(".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.ui.editors.prefs", props -> {
			props.put("eclipse.preferences.version", "1");
			props.put("showCarriageReturn", "false");
			props.put("showLineFeed", "false");
			props.put("showWhitespaceCharacters", "true");
		});
	}

	/** Sets the theme to be the classic eclipse look. */
	public void classicTheme() {
		extension.workspaceProp(".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.e4.ui.css.swt.theme.prefs", props -> {
			props.put("eclipse.preferences.version", "1");
			props.put("themeid", "org.eclipse.e4.ui.css.theme.e4_classic");
		});
	}
}
