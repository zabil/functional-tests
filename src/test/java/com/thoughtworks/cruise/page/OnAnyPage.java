/*************************GO-LICENSE-START*********************************
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.cruise.page;

import com.thoughtworks.cruise.Regex;
import com.thoughtworks.cruise.SahiBrowserWrapper;
import com.thoughtworks.cruise.Urls;
import com.thoughtworks.cruise.state.ScenarioState;
import com.thoughtworks.cruise.utils.Assertions;
import com.thoughtworks.cruise.utils.Assertions.Function;
import com.thoughtworks.cruise.utils.Assertions.Predicate;
import com.thoughtworks.cruise.utils.ScenarioHelper;
import com.thoughtworks.cruise.utils.Timeout;
import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import org.hamcrest.core.Is;
import org.junit.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OnAnyPage extends CruisePage {
	private boolean autoRefresh;

	private final ScenarioHelper scenarioHelper;
	private SahiBrowserWrapper browserWrapper;

	public OnAnyPage(ScenarioState state, ScenarioHelper scenarioHelper, Browser browser) {
		super(state, browser);
		this.scenarioHelper = scenarioHelper;
		this.browserWrapper = new SahiBrowserWrapper(browser);
	}

	public void reloadPage() {
		browserWrapper.reload();
	}
	
	@com.thoughtworks.gauge.Step("Logout - On Any Page")
	public void logout() {
		super.logout();
	}
	
	@com.thoughtworks.gauge.Step("Verify there are at least <count> errors")
	public void verifyThereAreAtLeastErrors(final Integer count) throws Exception {
		Assertions.waitUntil(Timeout.THREE_MINUTES, new Predicate() {
			@Override
			public boolean call() throws Exception {
				if (!autoRefresh) {
					reloadPage();
				}
				ElementStub errors = errorCountElement();
				if (!errors.exists()) {
					return false;
				}
				int actualErrors = Integer.parseInt(errors.getText().trim().substring("Errors: ".length()).trim());
				return actualErrors >= count;
			}
		});
	}

	@com.thoughtworks.gauge.Step("Verify there are <count> warnings")
	public void verifyThereAreWarnings(final Integer count) throws Exception {
		Assertions.waitUntil(Timeout.FIVE_MINUTES, new Predicate() {
			@Override
			public boolean call() throws Exception {
				if (!autoRefresh) {
					reloadPage();
				}
				ElementStub warnings = warningCountElement();
				if (!warnings.exists()) {
					return false;
				}
				return warnings.getText().trim().equals("Warnings: " + count);
			}
		});
	}
	
	@com.thoughtworks.gauge.Step("Verify there are no warnings")
	public void verifyThereAreNoWarnings() throws Exception {
		for (int i = 0; i < 3; i++) { // warnings should not appear over 3 page reloads		    
			Assert.assertThat(warningsExist(), Is.is(false));
		}
	}

    private boolean warningsExist() {
    	if (!autoRefresh) {
    		reloadPage();
    	}
        ElementStub warnings = warningCountElement();
        return warnings.exists();
    }

	private ElementStub warningCountElement() {
		return browser.span(Regex.wholeWord("warning_count")).in(messagesElement());
	}
	
	private ElementStub errorCountElement() {
		return browser.span("/error_count/").in(messagesElement());
	}
	
	//AskNarayan: messages don't appear consistently, need to refresh page to see it.
	@com.thoughtworks.gauge.Step("Open error and warning messages popup")
	public void openErrorAndWarningMessagesPopup() throws Exception {
		Assertions.waitUntil(Timeout.ONE_MINUTE, new Predicate() {
			@Override
			public boolean call() {
				if (!autoRefresh) {
					reloadPage();
				}
				ElementStub messagesLink = browser.link(0).in(messagesElement());
				if (messagesLink.exists()) {
					messagesLink.click();
					return true;
				} else {
					return false;
				}
			}			
		});		
	}

	@com.thoughtworks.gauge.Step("Verify there are no error messages")
	public void verifyThereAreNoErrorMessages() throws Exception{
		Assertions.waitUntil(Timeout.ONE_MINUTE, new Predicate() {
			@Override
			public boolean call() {
				if (!autoRefresh) {
					reloadPage();
				}
				return !errorCountElement().exists();
			}			
		});		
	}

	private ElementStub messagesElement() {
		return browser.div("cruise_message_counts");
	}
	
    @com.thoughtworks.gauge.Step("Wait for warnings to disappear")
	public void waitForWarningsToDisappear() throws Exception {
    	Assertions.waitFor(Timeout.NINETY_SECONDS, new Function<Boolean>() {
			
			@Override
			public Boolean call() {
				for (int i = 0; i < 5; i++) {
					warningsExist();
				}
				return !warningsExist();
			}
		});
    }

	@com.thoughtworks.gauge.Step("Turn on autoRefresh - On Any Page")
	public void turnOnAutoRefresh() throws Exception {
		this.autoRefresh = true;
		String url = browserWrapper.getCurrentUrl();
		if(!url.contains("autoRefresh")) {
			url = url + "?autoRefresh=true";
		} else {
			url = url.replaceAll("autoRefresh=false", "autoRefresh=true");
		}
		browser.navigateTo(url, true);
	}

	@com.thoughtworks.gauge.Step("Turn off autoRefresh - On Any Page")
	public void turnOffAutoRefresh() throws Exception {
		this.autoRefresh = false;
		String url = browserWrapper.getCurrentUrl();
		if(!url.contains("autoRefresh")) {
			url = url + "?autoRefresh=false";
		} else {
			url = url.replaceAll("autoRefresh=true", "autoRefresh=false");
		}
		browser.navigateTo(url, true);
	}
    
    @com.thoughtworks.gauge.Step("Stop <numberOfJobs> jobs waiting for file")
	public void stopJobsWaitingForFile(int numberOfJobs) throws Exception {
    	scenarioHelper.stopJobsThatAreWaitingForFileToExist(numberOfJobs);
    }

    @com.thoughtworks.gauge.Step("Verify admin link is disabled")
	public void verifyAdminLinkIsDisabled() throws Exception {
        ElementStub adminLink = browser.link("ADMIN");
        assertThat(adminLink.exists(), is(false));
    }
    
    public void openAdminPage(){
    	browser.link("ADMIN").click();
    }

	@com.thoughtworks.gauge.Step("Verify <url> returns <httpCode>")
	public void verifyReturns(String url, Integer httpCode) throws Exception {
		browser.navigateTo(Urls.urlFor(url));
		ElementStub error = browser.heading1(Integer.toString(httpCode));
		Assert.assertThat(error.isVisible(), Is.is(true));
	}

	@com.thoughtworks.gauge.Step("Logout and login as <username>")
	public void logoutAndLoginAs(String username) throws Exception {
		logout();
		new OnLoginPage(scenarioState, scenarioHelper, browser).loginAs(username);
	}

	public void open() {
	}
	
	protected String url() {
		return "";
	}

	public void verifyOnLoginPageWithMessage(String errorMessage) throws Exception {
		OnLoginPage. assertLoginErrorDisplayed(browser, errorMessage);
	}

	@com.thoughtworks.gauge.Step("Wait and verify there are no warnings")
	public void waitAndVerifyThereAreNoWarnings() throws Exception {
		Assertions.waitUntil(Timeout.TWO_MINUTES, new Predicate() {
			@Override
			public boolean call() throws Exception {
				verifyThereAreNoWarnings();
				return true;
			}			
		});
	}

    @com.thoughtworks.gauge.Step("Sleep for <secs> seconds")
	public void sleepForSeconds(Integer secs) throws Exception {
        Thread.sleep(secs * 1000);
    }
}
