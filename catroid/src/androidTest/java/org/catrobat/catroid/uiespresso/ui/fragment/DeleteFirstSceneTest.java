/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DeleteFirstSceneTest {
	private String secondSceneName = "Scene2";
	private String projectName = "DeleteScenesTest";
	private String firstSpriteName = "FIRST";
	private String backgroundName = "BACKGROUND2";

	@Rule
	public BaseActivityTestRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectActivity.class, false, false);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
	}

	@Before
	public void setUp() throws Exception {
		createProject(ApplicationProvider.getApplicationContext(), projectName);
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void deleteFirstSceneTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm))
				.perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_scenes, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.check(matches(isDisplayed()))
				.perform(click());

		assertEquals(1, ProjectManager.getInstance().getCurrentProject().getSceneList().size());

		onView(withText(secondSceneName))
				.check(doesNotExist());

		onView(withText(backgroundName))
				.check(matches(isDisplayed()));

		onView(withId(R.id.empty_view))
				.check(matches(isDisplayed()));
	}

	private void createProject(Context context, String projectName) {
		Project project = new Project(context, projectName);
		Scene scene1 = project.getDefaultScene();
		Sprite sprite1 = new Sprite(firstSpriteName);
		Script script1 = new StartScript();
		script1.addBrick(new SetXBrick());
		sprite1.addScript(script1);
		scene1.addSprite(sprite1);

		Scene scene2 = new Scene(secondSceneName, project);
		Sprite sprite2 = new Sprite(backgroundName);
		Script script2 = new StartScript();
		script2.addBrick(new SetXBrick());
		sprite2.addScript(script2);
		scene2.addSprite(sprite2);
		project.addScene(scene2);
		ProjectManager.getInstance().setCurrentProject(project);
		saveProjectSerial(project, ApplicationProvider.getApplicationContext());
	}
}
