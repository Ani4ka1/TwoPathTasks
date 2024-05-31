import telebot
from telebot import types

bot = telebot.TeleBot('6715155343:AAHLXZMkOIJhppoUvcQ3HAevAh-9Mt7E38M')

@bot.message_handler(commands=['start'])
def start(message):
    markup = types.ReplyKeyboardMarkup(resize_keyboard=True, row_width=1)
    markup.add("Часто задаваемые вопросы 👀")
    mess = f'Здравстуйте, <em><b>{message.from_user.first_name}</b></em>!❤\n \nОпишите проблему, которая у вас возникла при использовании приложения <b>"TwoPathTasks"</b>📒🖇\n \nОзнакомьтесь с ответами на наиболее часто задаваемые вопросы ниже👇'
    bot.send_message(message.chat.id, mess, parse_mode='html', reply_markup=markup)

@bot.message_handler(content_types=['text'])
def get_user_text(message):
    if message.text == "Часто задаваемые вопросы 👀":
        bot.send_message(message.chat.id, "❓<b>Кто будет делать дела, которые я запишу в приложение?</b> "
                                          "\n<b>Ответ:</b> К сожалению, дела придётся делать вам, но вы не расстраивайтесь, у вас все получится❤ \n "
                                          "\n❓<b>А зачем мне это приложение?</b>\n<b>Ответ:</b> Чтобы повысить вашу эффективность в выполнение дел <strike>и чтобы перестать лениться</strike>\n "
                                          "\n❓<b>А что означают категории в приложении?</b>\n<b>Ответ:</b><strike> Чумба, ты дурак?</strike> Категории - это удобный инструмент, который помогает разграничивать ваши дела", parse_mode='html')
    else:
        bot.send_message(message.chat.id, "Передаю ваш запрос <b>специалисту</b>, подождите пару минут!❤",parse_mode='html')

bot.polling(none_stop=True)
