package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.corvus.types.CorvusFileExtension;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.fornax.utility.HTTPUtility;
import com.hadenwatne.fornax.utility.models.HTTPResponse;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GIF extends Command {
	private Shmames shmames;
	public GIF(Shmames shmames) {
		super(false);

		this.shmames = shmames;
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("gif", "Send an awesome, randomly-selected GIF based on a search term.")
				.addParameters(
						new CommandParameter("search", "What to find a GIF for.", ParameterType.STRING)
								.setExample("bob ross")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {
		CorvusBuilder builder = Corvus.error(execution.getBot());
		String errorMessage = execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.GENERIC_ERROR.name());

		builder.addBreadcrumbs(this.getCommandStructure().getName())
				.setDescription(errorMessage)
				.setEphemeral();

		Corvus.reply(execution, builder);
	}

	@Override
	public void run(Execution execution) {
		String search = execution.getArguments().get("search").getAsString();
		String filter = "high";

		if(execution.getChannel().asTextChannel().isNSFW()) {
			filter = "low";
		}

		String gifUrl = getGIF(search, filter);
		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName(), search)
						.setImage(gifUrl, CorvusFileExtension.GIF);

		Corvus.reply(execution, builder);
	}

	private String getGIF(String search, String filter) {
		App.getLogger().Log(LogType.NETWORK, "[GIF Search: " + search + " @ " + filter + "]");

		search = StringEscapeUtils.ESCAPE_HTML4.translate(search.trim());
		String apiKey = shmames.getBrainController().getMotherBrain().getTenorAPIKey();
		HTTPResponse result = HTTPUtility.get("https://g.tenor.com/v1/search?q=" + search + "&key=" + apiKey + "&contentfilter=" + filter + "&limit=25");

		if(result.responseCode() == 200) {
			JSONArray resultSet = new JSONObject(result.response()).getJSONArray("results");
			List<JSONArray> gifMedia = new ArrayList<>();

			// Add the media array of each result.
			for (int i = 0; i < resultSet.length(); i++) {
				gifMedia.add(resultSet.getJSONObject(i).getJSONArray("media"));
			}

			List<String> gifURLs = new ArrayList<>();

			// For each media array, add the gif value.
            for (JSONArray media : gifMedia) {
                for (int o = 0; o < media.length(); o++) {
                    JSONObject resultObject = media.getJSONObject(o);
                    JSONObject gif = resultObject.getJSONObject("gif");
                    JSONObject mediumgif = resultObject.getJSONObject("mediumgif");
                    JSONObject tinygif = resultObject.getJSONObject("tinygif");
                    String url;

                    if (gif.getInt("size") <= 8000000) {
                        url = gif.getString("url");
                    } else if (mediumgif.getInt("size") <= 8000000) {
                        url = mediumgif.getString("url");
                    } else {
                        url = tinygif.getString("url");
                    }

                    gifURLs.add(url);
                }
            }

			if (!gifURLs.isEmpty()) {
				return RandomService.GetRandomObjectFromList(gifURLs);
			} else {
				return getGIF("404", "high");
			}
		} else {
			return "https://media.tenor.com/qsthhHhdjsQAAAAC/error-windows.gif";
		}
	}
}
