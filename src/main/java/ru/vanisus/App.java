package ru.vanisus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class App{
 	public static void main(String[] args) {
		String TOKEN = "5762429327:AAH4l3q-DDPZywqhXKhcGWzgKFfPU2A66yY";
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
						User user;
						try {
							user = new User(update.message().from().firstName(), new URL(url));
						} catch (MalformedURLException e) {
							throw new RuntimeException(e);
						}

						users.put(userId, user);
						bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, теперь вы можете проссматривать свое расписание благодаря мне :)"));
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
