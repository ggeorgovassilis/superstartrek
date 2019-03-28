package superstartrek.client.pwa;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class InstallationPrompt extends Composite{

	public InstallationPrompt(PWA pwa) {
		HTMLPanel panel = new HTMLPanel("<div class='label'>Add Super Star Trek short cut to home?</div><span id=binstall></span><span id=bdismiss></span>");
		panel.addStyleName("install-pwa-prompt");
		Button installButton = new Button("Install");
		Button dismissButton = new Button("Dismiss");
		panel.add(installButton, "binstall");
		panel.add(dismissButton, "bdismiss");
		
		installButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				pwa.installApplication();
			}
		});
		
		dismissButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				pwa.dismissInstallationPrompt();
			}
		});
		initWidget(panel);
	}
}
