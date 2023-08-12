package com.myflexbox.views;

import com.myflexbox.MessageUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * MainLayout class defines the overall layout of the application.
 * It consists of the drawer content and the header content, including
 * logos and navigation components.
 */
@CssImport("./themes/csvimporter/components/instructions.css")
@UIScope
public class MainLayout extends AppLayout implements RouterLayout {

    private H2 viewTitle;

    /**
     * Constructs the MainLayout by initializing the primary section, drawer, header, and other components.
     */
    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    /**
     * Adds the header content to the layout.
     * Includes the drawer toggle button, logo, and view title.
     */
    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        H1 logoText = new H1("MYFLEXBOX");
        Image logoImage = new Image("themes/csvimporter/images/myflexbox_logo.jpg", "Logo alt text");
        logoImage.setHeight("3.5em");
        logoImage.setWidth("3.5em");

        HorizontalLayout logoLayout = new HorizontalLayout(logoImage, logoText);
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        addToNavbar(true, toggle, logoLayout, viewTitle);
    }

    /**
     * Adds the drawer content to the layout.
     * This content includes the application name and instructions.
     */
    private void addDrawerContent() {
        H1 appName = new H1("CSVImporter");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        String csvUploadInstruction = MessageUtil.getMessage("csv_upload_instruction");
        String gridDataInfo = MessageUtil.getMessage("grid_data");
        String gridIgnoreValueInfo = MessageUtil.getMessage("grid_ignore_value");
        String gridNote = MessageUtil.getMessage("grid_note");

        VerticalLayout instructionLayout = new VerticalLayout(
                new Span(csvUploadInstruction),
                new Span(gridDataInfo),
                new Span(gridIgnoreValueInfo),
                new Span(gridNote)
        );
        instructionLayout.setSpacing(true);
        instructionLayout.addClassName("instruction-layout");

        VerticalLayout drawerContent = new VerticalLayout(header, instructionLayout);
        addToDrawer(drawerContent);
    }

    /**
     * Called after navigation to update the view title according to the current page.
     */
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Gets the title of the current page from the PageTitle annotation.
     *
     * @return the title of the current page or an empty string if the title is not available.
     */
    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
