package ru.vanisus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.image.BufferedImage;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App{
 	public static void main(String[] args) {
		String TOKEN = "5778374647:AAHIgjz3HqT7OEh1L2Tf-pt88PccHhvvCyI";
		Map<Long, User> users = new HashMap<>();
		TelegramBot bot = new TelegramBot(TOKEN);

		bot.setUpdatesListener(updates -> {
			updates.forEach(System.out::println);
			updates.forEach(update -> {
				Long userId = update.message().from().id();
				if(!users.containsKey(userId)) {
					bot.execute(new SendMessage(update.message().chat().id(), "Вам необходимо прислать ссылку на расписание вашей группы"));
					users.put(userId, null);
				}
				else if(users.get(userId) == null) {
					String rightUrl = "https://rasp.sstu.ru/rasp/group/";
					String url = update.message().text();
					String testUrl = url.replaceAll("[0-9]", "");
					if (!(testUrl.equals(rightUrl) && urlValidator(url)))
						bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
					//bot.execute(new SendMessage(update.message().chat().id(), testUrl));
					else {
						User user = new User(update.message().from().firstName(), url);

						users.put(userId, user);
						bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, теперь вы можете проссматривать свое расписание благодаря мне :)"));
						Document doc = null;
						try {
							doc = Jsoup.connect(user.getUrl()).get();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						int count = 0;
						List<String> timetable = new ArrayList<>();

						String h1 = doc.select("div.week" ).html();
						String div = doc.select("div.week > div.day-current").html();
						HtmlImageGenerator hmg = new HtmlImageGenerator();
						hmg.loadHtml(div);
						hmg.saveAsImage("test.png");

						bot.execute(new SendMessage(update.message().chat().id(), div));
						//bot.execute(new SendPhoto(update.message().chat().id(), resources));

					}
				}
				else {
					System.out.println(update.toString());
				}
			});
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	public static boolean urlValidator(String url)
	{
		try {
			new URL(url).toURI();
			return true;
		}
		catch (URISyntaxException | MalformedURLException exception) {
			return false;
		}
	}
}
