package ru.vanisus;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.vanisus.DAO.*;

public class App {
 	public static void main(String[] args) {
		String TOKEN = "6108997566:AAFx0BbXz8M2b2AAYkEZw2D1z3D85xdwAvM";
		TelegramBot bot = new TelegramBot(TOKEN);

		ZonedDateTime nowInSamara = ZonedDateTime.now(ZoneId.of("Europe/Samara"));
		LocalDateTime targetTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 28));
		ZonedDateTime targetTimeInSamara= ZonedDateTime.of(targetTime, ZoneId.of("Europe/Samara"));
		DayOfWeek date = LocalDate.EPOCH.getDayOfWeek();

		if (nowInSamara.isAfter(targetTimeInSamara)) {
			targetTimeInSamara = targetTimeInSamara.plusDays(1);
		}
		long delayInMillis = Duration.between(nowInSamara, targetTimeInSamara).toMillis();

		bot.setUpdatesListener(updates -> {
			updates.forEach(System.out::println);
				updates.forEach(update -> {
				Document doc = null;
				Element day;
				Element week;
				String div;

				ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
				executor.schedule(() -> {
					Document docShedule;
					Element dayShedule;
					String divShedule;
					System.out.println(date.getValue());
					if(isShedule(update.message().from().id())) {
						if(isMessageExists(update.message().chat().id()))
							bot.execute(new DeleteMessage(update.message().chat().id(), getMessageId(update.message().chat().id())));
						try {
								docShedule = Jsoup.connect(getURL(update.message().from().id())).get();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						dayShedule = docShedule.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
						divShedule = getDayTimetable(dayShedule);
						bot.execute(new SendMessage(update.message().chat().id(), "Расписание на завтра: " + LocalDate.now().plusDays(1) + "\n\n" + divShedule));
						if(isMessageExists(update.message().chat().id()))
							updateMessage(update.message().chat().id(), (long) (update.message().messageId() + 1));
						else
							addMessage(update.message().chat().id(), (long) (update.message().messageId() + 1));
						}
					executor.shutdown();
				}, delayInMillis, TimeUnit.MILLISECONDS);

					if (!findId(update.message().from().id())) {
						if(update.message().text().equalsIgnoreCase("/start"))
							bot.execute(new SendMessage(update.message().chat().id(), "Вам необходимо прислать ссылку на расписание вашей группы"));
						String url = update.message().text();
						System.out.println(url);
						if(!url.equalsIgnoreCase("/start")) {
							if(urlChecker(url)) {
								bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, теперь вы можете просматривать свое расписание благодаря мне :)"));
								//User user = new User(update.message().from().id(), update.message().from().firstName(), url);
								addUser(update.message().from().id(), url);
							} else
								bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
						}
				}
				if(findId(update.message().from().id())) {
					if (update.message().text().equalsIgnoreCase("/today")) {
//					bot.execute(new SendMessage(update.message().chat().id() ,"ЛАЛАЛА Я СЛОМАЛСЯ"));
						try {
							doc = Jsoup.connect(getURL(update.message().from().id())).get();
						} catch (IOException e) {
							bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
						}
						assert doc != null;
						day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()));
						div = getDayTimetable(day);
						bot.execute(new SendMessage(update.message().chat().id(), div));

					}
					if (update.message().text().equalsIgnoreCase("/tomorrow")) {
						try {
							doc = Jsoup.connect(getURL(update.message().from().id())).get();
						} catch (IOException e) {
							bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
						}
						assert doc != null;
						day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
						div = getDayTimetable(day);
						bot.execute(new SendMessage(update.message().chat().id(), div));
					}
					if (update.message().text().equalsIgnoreCase("/currentweek")) {
//					bot.execute(new SendMessage(update.message().chat().id(), "ЫЫЫ"));
						try {
							doc = Jsoup.connect(getURL(update.message().from().id())).get();
						} catch (IOException e) {
							bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
						}
						assert doc != null;
						week = doc.select("div.week").get(0);
						div = getWeekTimetable(week);
						bot.execute(new SendMessage(update.message().chat().id(), div));
					}

					if (update.message().text().equalsIgnoreCase("/nextweek")) {
						try {
							doc = Jsoup.connect(getURL(update.message().from().id())).get();
						} catch (IOException e) {
							bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
						}
						try {
							assert doc != null;
							week = doc.select("div.week").get(1);
							div = getWeekTimetable(week);
							bot.execute(new SendMessage(update.message().chat().id(), div));
						} catch (IndexOutOfBoundsException e) {
							bot.execute(new SendMessage(update.message().chat().id(), "Расписание на следующую неделю пока недоступно :)"));
						}

					}

					if (update.message().text().equalsIgnoreCase("/shedule")) {
						Long temp = update.message().from().id();
						updateShedule(temp, !isShedule(temp));
						if (isShedule(temp))
							bot.execute(new SendMessage(update.message().chat().id(), "Теперь вы будете получать сообщение с расписанием на завтрашний день"));
						else
							bot.execute(new SendMessage(update.message().chat().id(), "Теперь вы не будете получать сообщение с расписанием на завтрашний день"));

					}

					if (update.message().text().equalsIgnoreCase("/help")) {
						bot.execute(new SendMessage(update.message().chat().id(), "Сам себе помоги"));
					}


					if (update.message().text().equalsIgnoreCase("/updatelink")) {
						bot.execute(new DeleteMessage(update.message().chat().id(), update.message().messageId()));
						bot.execute(new SendMessage(update.message().chat().id(), "Пока нет такой возможности :("));
//						bot.execute(new SendMessage(update.message().chat().id(), "Хорошо, пришлите мне новую ссылку"));
//						div = update.message().text();
//						if (!update.message().text().equalsIgnoreCase("/updatelink")) {
//							if (urlChecker(div)) {
//								updateURL(update.message().from().id(), div);
//								bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, ссылка обновлена :)"));
//							} else
//								bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
//						}
					}
				}


	}); return UpdatesListener.CONFIRMED_UPDATES_ALL; });
	 }
	public static Boolean urlChecker( String url) {
		String rightUrl = "https://rasp.sstu.ru/rasp/group/";
		String testUrl = url.replaceAll("\\d", "");
		return testUrl.equals(rightUrl) && urlValidator(url) && url.length() != 0;
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

	 public static int getDayNumberNew(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		return day.getValue();
	}

	public static String getDayTimetable(Element day)
	{
		StringBuilder dayTimetable = new StringBuilder();
		try {
			for (int count = 0; count < day.select("div.day-lesson").size(); count++) {
				String resultingLesson = day.select("div.day-lesson").get(count).text();
				if(!(resultingLesson.length() < 1)) {
					dayTimetable.append(resultingLesson);
					dayTimetable.append("\n\n");
				}
			}
		} catch (NullPointerException e)
		{
			System.out.println("В этом дне только 4 пары");
		}
		//String resultDay = new String(dayTimetable);
		return dayTimetable.toString();
	}

	public static String getWeekTimetable(Element week) {
		 StringBuilder weekTimetable = new StringBuilder();
		 for(int count = 1; count < 7; count++) {
			 StringBuilder resultingDay = new StringBuilder();
			 Element dayE = week.select("div.day").get(count);
			 String dateOfTheDay = dayE.select("div.day > div.day-header").get(0).text();
			 if (dateOfTheDay.length() != 0) {
				 weekTimetable.append("// ").append(dateOfTheDay).append("// ");
				 weekTimetable.append("\n\n");
				 resultingDay.append(getDayTimetable(dayE));
				 weekTimetable.append(resultingDay);
				 weekTimetable.append("\n\n");
			 }
		 }
		 return weekTimetable.toString();
	}
}
