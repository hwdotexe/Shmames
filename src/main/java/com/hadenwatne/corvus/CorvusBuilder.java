package com.hadenwatne.corvus;

import com.hadenwatne.corvus.types.CorvusFileExtension;
import com.hadenwatne.corvus.types.MessageType;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CorvusBuilder {
    private final String NAVIGATION_INDICATOR = " » ";
    private final Bot bot;
    private final MessageType type;
    private final EmbedBuilder embedBuilder;
    private final List<String> breadcrumbs;
    private final List<CorvusAttachment> attachments;
    private final List<LayoutComponent> layoutComponents;
    private Message message;
    private boolean useBreadcrumbs;
    private boolean useCustomAuthor;
    private boolean ephemeral;
    private boolean mentionAuthor;
    private boolean isPrebuilt;

    CorvusBuilder(Bot bot, MessageType type) {
        this.bot = bot;
        this.type = type;
        this.embedBuilder = new EmbedBuilder();
        this.breadcrumbs = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.layoutComponents = new ArrayList<>();
        this.useBreadcrumbs = false;
        this.useCustomAuthor = false;
        this.ephemeral = false;
        this.mentionAuthor = false;
        this.isPrebuilt = false;

        this.embedBuilder.setColor(this.type.getColor());
    }

    MessageType getType() {
        return type;
    }

    List<CorvusAttachment> getAttachments() {
        return attachments;
    }

    List<LayoutComponent> getLayoutComponents() {
        return layoutComponents;
    }

    Message getMessage() {
        return message;
    }

    boolean isEphemeral() {
        return ephemeral;
    }

    boolean isMentionAuthor() {
        return mentionAuthor;
    }

    boolean hasAttachments() {
        return !this.attachments.isEmpty();
    }

    void setMessage(Message message) {
        this.message = message;
    }

    public CorvusBuilder addBreadcrumbs(String... breadcrumb) {
        this.useBreadcrumbs = true;
        this.breadcrumbs.addAll(List.of(breadcrumb));

        return this;
    }

    public CorvusBuilder setCustomAuthor(String authorName, String imageUrl, CorvusFileExtension extension) {
        this.useCustomAuthor = true;

        return setAuthor(authorName, imageUrl, extension);
    }

    CorvusBuilder setAuthor(String authorName, String imageUrl, CorvusFileExtension extension) {
        try {
            URL file = URI.create(imageUrl).toURL();
            String fileName = "_CORVUS_PROFILE_IMAGE_" + extension.getExtension();

            this.attachments.add(new CorvusAttachment(file, fileName));
            this.embedBuilder.setAuthor(authorName, null, "attachment://" + fileName);
        } catch (MalformedURLException exception) {
            App.getLogger().LogException(exception);
        }

        return this;
    }

    public CorvusBuilder mentionAuthor() {
        this.mentionAuthor = true;

        return this;
    }

    public CorvusBuilder setEphemeral() {
        this.ephemeral = true;

        return this;
    }

    public CorvusBuilder attach(URL fileURL, String fileName) {
        this.attachments.add(new CorvusAttachment(fileURL, fileName));

        return this;
    }

    public CorvusBuilder setImage(String url, CorvusFileExtension extension) {
        try {
            URL file = URI.create(url).toURL();
            String fileName = "_CORVUS_IMAGE_" + extension.getExtension();

            this.attachments.add(new CorvusAttachment(file, fileName));
            this.embedBuilder.setImage("attachment://" + fileName);
        } catch (MalformedURLException exception) {
            App.getLogger().LogException(exception);
        }

        return this;
    }

    public CorvusBuilder setImage(String url) {
        this.embedBuilder.setImage(url);

        return this;
    }

    public CorvusBuilder setThumbnail(String url, CorvusFileExtension extension) {
        try {
            URL file = URI.create(url).toURL();
            String fileName = "_CORVUS_THUMBNAIL_" + extension.getExtension();

            this.attachments.add(new CorvusAttachment(file, fileName));
            this.embedBuilder.setThumbnail("attachment://" + fileName);
        } catch (MalformedURLException exception) {
            App.getLogger().LogException(exception);
        }

        return this;
    }

    public CorvusBuilder setThumbnail(String url) {
        this.embedBuilder.setThumbnail(url);

        return this;
    }

    public CorvusBuilder addLayoutComponent(LayoutComponent layoutComponent) {
        this.layoutComponents.add(layoutComponent);

        return this;
    }

    public CorvusBuilder addField(String name, String value, boolean inline) {
        this.embedBuilder.addField(name, value, inline);

        return this;
    }

    public CorvusBuilder setDescription(String description) {
        this.embedBuilder.setDescription(description);

        return this;
    }

    EmbedBuilder preBuild() {
        if(!this.isPrebuilt) {
            if (!useCustomAuthor) {
                if (this.useBreadcrumbs) {
                    StringBuilder breadcrumbText = new StringBuilder();

                    breadcrumbText.append(this.bot.getBotName());

                    for (String crumb : this.breadcrumbs) {
                        if (!breadcrumbText.isEmpty()) {
                            breadcrumbText.append(NAVIGATION_INDICATOR);
                            breadcrumbText.append(crumb);
                        }
                    }

                    String authorText = breadcrumbText.toString();
                    authorText = authorText.substring(0, Math.min(authorText.length(), MessageEmbed.AUTHOR_MAX_LENGTH) - 1);

                    this.setAuthor(authorText, this.bot.getBotAvatarUrl(), CorvusFileExtension.PNG);
                } else {
                    this.setAuthor(this.bot.getBotName(), this.bot.getBotAvatarUrl(), CorvusFileExtension.PNG);
                }
            }

            this.isPrebuilt = true;
        }

        return this.embedBuilder;
    }
}
