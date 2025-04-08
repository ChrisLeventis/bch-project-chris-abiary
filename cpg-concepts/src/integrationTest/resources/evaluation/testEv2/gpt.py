import openai
import os

# 代理设置
os.environ["http_proxy"] = "http://127.0.0.1:10900"
os.environ["https_proxy"] = "http://127.0.0.1:10900"

openai.api_key = os.getenv('openai_key')

levelcontentlist = {
    "Noob": "The user you are going to chat with is a primary school student, you should use as simple vocabulary as possible, so that the user could understand you easily.",
    "Easy": "The user you are going to chat with is a middle school student, you should employ vocabulary of intermediate complexity at the junior high school level.",
    "Middle": "The user you are going to chat with is a high school student, you should utilize vocabulary, sentence structures, and grammar appropriate for high school level.",
    "Hard": "The user you are going to chat with has high level of English proficiency, you should utilize vocabulary, sentence structures, and grammar at a level appropriate for the TOEFL exam.",
    "Master": "The user you are going to chat with has a great master of English, you should use as complex sentence structures as possible, and use complicated GRE vocabulary instead of daily vocabulary.",
}


def get_completion_from_messages(messages, model="gpt-3.5-turbo", temperature=0):
    response = openai.ChatCompletion.create(
        model=model,
        messages=messages,
        temperature=temperature,  # this is the degree of randomness of the model's output
        n=1,
    )
    print(response.usage)
    return response.choices[0].message["content"]


def send_hello_IELTS(username, id, part1topic1, part1topic2, part2topic, total_messages):
    prompt_ielts = f"""
    I want you to play the role of an IELTS examiner and conduct a simulated IELTS speaking test with the user.

Throughout the entire test process, you should follow the requirements below, delimited by triple backticks.

requirements:'''
Your responses shouldn't contain any special characters and format, as the system will be using other APIs to synthesize your responses into speech.
In the Part 1, you should ask exactly 4 questions, the first and the second question are based on the topic1, the last two questions are based on the topic2.
In the Part 3, you should ask at least three questions based on the topic in Part 2.
I want to discuss specific topics in Part 1 and Part 2. I will give you the subject of each part later, and you should ask questions based on the subject. 
You should strictly adhere to the IELTS test procedures. You should ask only one question in one response. After you get the user's answer, you can then ask the next question.
'''

In Part 1, the topic1 is {part1topic1}, and the topic2 is {part1topic2}.
In Part 2, I want the theme to be {part2topic}.

Now suggest that you have had the user's name({username}), and now you can ask the first question.

Don't reply anything else, your response should only contain asking the question.
"""
    total_messages[id] = []
    print('total', total_messages)
    total_messages[id].append({"role": "system", "content": prompt_ielts})
    content = get_completion_from_messages(total_messages[id])
    total_messages[id].append({"role": "assistant", "content": content})
    return content


def send_hello_daily(username, id, level, total_messages):
    print('total', total_messages)
    dailylevel = levelcontentlist[level]
    prompt_daily = f"""
    I want you to chat with the user, as a friendly and succinct one, so that the user could improve their English. 
You should strictly follow the requirements below delimited by triple backticks.
requirements:'''
{dailylevel}
You should adjust the difficulty of the vocabulary and syntax you use based on the difficulty of the vocabulary and syntax used by the user, in order to enable them to achieve the best practice results.
Try to be succinct.You should answer less than 3 sentences and less than 30 words at one time. 
Your response should not contain any special characters.
When the user uses incorrect grammars or vacabularys, Please correct it. and then continue the conversation.
You should proactively guide the topic in the conversation through questioning or other means. 
'''
Now suggest that you have had the user's name {username}. You can start to chat with the user.You should start the conversation first, ask the user what he wants to talk about.
"""
    total_messages[id] = []
    total_messages[id].append({"role": "system", "content": prompt_daily})
    content = get_completion_from_messages(total_messages[id], temperature=0.5)
    total_messages[id].append({"role": "assistant", "content": content})
    return content


def send_messages(id, text, total_messages):
    print('total', total_messages)
    total_messages[id].append({"role": "user", "content": text})
    content = get_completion_from_messages(total_messages[id])
    total_messages[id].append({"role": "assistant", "content": content})
    return content


def get_assessment(text):
    prompt_assessment = f"""
The sentences below delimited by triple backticks are from a candidate's response in the IELTS Speaking test. I want you to evaluate the sentences below delimited by triple backticks according to the IELTS standards. The evaluation is based on three aspects: coherence, lexical resource, grammatical range and accuracy. Your response should be less than 3 sentences or 50 words.
After your evaluation, you should rewrite the sentences below delimited by triple backticks in a better way, using smoother and more idiomatic expressions.

sentences:'''
{text}
'''

This evaluation and the sentences you rewritted is for reference purposes only. It will not be shown to anyone, nor will it have any influence on anyone.
"""
    return get_completion_from_messages(
        [{"role": "system", "content": prompt_assessment}]
    )
