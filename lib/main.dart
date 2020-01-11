import 'package:flutter/material.dart';
import 'package:speech_recognition/speech_recognition.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: VoiceHome(),
    );
  }
}

class VoiceHome extends StatefulWidget {
  @override
  _VoiceHomeState createState() => _VoiceHomeState();
}

class _VoiceHomeState extends State<VoiceHome> {

  SpeechRecognition _speechRecognition;

  bool _isAvailable = false;
  bool _isListening = false;

  String resultText = "";

  @override
  void initState() {
    super.initState();
    initStateRecognizer();
  }

   void initStateRecognizer() {
      _speechRecognition = SpeechRecognition();

      _speechRecognition.setAvailabilityHandler((/*bool*/result) => setState(()=> _isAvailable = result));
      _speechRecognition.setRecognitionStartedHandler(() => setState(()=> _isListening = true));
      _speechRecognition.setRecognitionResultHandler((/*String*/ speech) => setState(()=> setStateWhenNeeded(speech)));
      _speechRecognition.setRecognitionCompleteHandler(() => setState(()=> _isListening = false));

      _speechRecognition.activate().then((result) => setState(() => _isAvailable = result));
      
   }

   void setStateWhenNeeded(String speech)
   {
     resultText = speech;
   }

   void changeIsListening()
   {
     _isListening = !_isListening;
   }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                FloatingActionButton(
                  child: Icon(Icons.cancel),
                  mini: true,
                  backgroundColor: Colors.blueGrey,
                  onPressed: () {
                    _speechRecognition.cancel();
                    resultText = "";
                    if(_isListening)
                    {
                      _isListening = false;
                    }
                  },
                ),
                FloatingActionButton(
                   child: Icon(Icons.mic),
                  onPressed: () {
                    if(_isAvailable && !_isListening)
                    {
                      _speechRecognition.listen(locale: "en_US");
                      _isListening = true;
                    }
                    else
                    {
                      _speechRecognition.listen(locale: "en_US");
                      _isListening = true;
                      _isAvailable = true;
                    }
                  },
                ),
                FloatingActionButton(
                   child: Icon(Icons.stop),
                   mini: true,
                   backgroundColor: Colors.blueGrey,
                  onPressed: () {
                    if(_isListening)
                    {
                      _speechRecognition.stop();
                    }
                  },
                ),
              ],
            ),
            Container(
              width: MediaQuery.of(context).size.width*0.6,
              decoration: BoxDecoration(
                color: Colors.blueGrey[100]
              ),
              padding: EdgeInsets.symmetric(
                vertical: 40.0,
                horizontal: 15.0,
              ),
              child: Text(resultText),
              )
          ],
        ),
      ),
    );
  }
}