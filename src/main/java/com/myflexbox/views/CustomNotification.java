package com.myflexbox.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Custom notification class for all notifications shown
 */
public class CustomNotification {

    private static Notification currentNotification;

    public static void show(String message, String variant) {
        // Close the existing notification if there is one
        if (currentNotification != null) {
            currentNotification.close();
        }

        // Create a new notification and apply the variant
        currentNotification = new Notification();
        currentNotification.setPosition(Notification.Position.TOP_END);
        currentNotification.setDuration(3000); // Can set duration if needed

        // Create a layout to hold the message and close button
        HorizontalLayout layout = new HorizontalLayout();
        Span messageSpan = new Span(message);
        layout.add(messageSpan);

        // Create a close button
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> currentNotification.close());
        layout.add(closeButton);

        currentNotification.add(layout);

        // Apply the variant
        switch (variant.toLowerCase()) {
            case "success" -> currentNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            case "error" -> currentNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            case "primary" -> currentNotification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        }

        currentNotification.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                currentNotification = null;
            }
        });
        currentNotification.open();
    }

    // Overloaded method to support the default case
    public static void show(String message) {
        show(message, "");
    }
}
