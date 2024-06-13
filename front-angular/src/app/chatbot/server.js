const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');
const app = express();

app.use(bodyParser.json());

const openaiApiKey = 'sk-proj-6yqfAO44DGnayXH4wBY4T3BlbkFJGksYZyvjURvQ4QytoKdT';

app.post('/webhook', async (req, res) => {
  const { message } = req.body;

  try {
    const response = await axios.post('https://api.openai.com/v1/engines/davinci-codex/completions', {
      prompt: message,
      max_tokens: 150
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${openaiApiKey}`
      }
    });

    const reply = response.data.choices[0].text.trim();
    res.send({ reply });
  } catch (error) {
    console.error(error);
    res.status(500).send('Error processing the request');
  }
});

app.listen(3000, () => {
  console.log('Server is running on port 3000');
});
