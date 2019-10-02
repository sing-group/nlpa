# Natural Language Pre-processing Architecture

NLPA is a plugin designed to operate in conjuction with BDP4J (https://github.com/sing-group/bdp4j) and able to extract texts from Twitter, Youtube Comments, text files, raw email files (.eml) or WARC (Web Archive) files. The extracted text can be preprocessed into a Dataset using task (org.bdp4j.pipe.Pipe) definitions. This framework incorporates more than 30 preprocessing tasks to transform the text. 

## Requirements

You need this user accounts:

* Babelnet / Babelfy
* Twitter
* Youtube

## Configuration files
To properly opearte with NLPA the following configuration files (and options) should be filled:

* `babelnet.var.properties`
    * `babelnet.dir` - Babelnet path. Directory where are babelnet files  .
    * `babelnet.key` - Babelnet key. If you log in babelnet website, this key is in RESTful information target.

* `babelfy.var.properties`
    * `babelfy.key` - The same key as Babelnet

* `configurations.ini`

```
[twitter] 
ConsumerKey=<YourConsumerKey> 
ConsumerSecret=<YourConsumerSecret>
AccessToken=<YourAccessToken>
AccessTokenSecret=<YourAccessTokenSecret>

[youtube]
APIKey=<YourYoutubeAPIKey>
```
We are aware that with youtube we are using unsecure API keys. Next versions will include more secure forms to access YouTube services (i.e. using OAuth schemes).

Please see files config/configurations.ini.example, config/babelnet.var.properties.example and config/babelfy.var.properties.example for further configuration details.

## Introducing NLPA

As stated before, to execute Text Analytics, big data should be transformed into full-featured datasets ready to be processed using AI techniques. Customizing and executing this transformation is the main functionality provided by NLPA. For this purpose, NLPA contains a list of pre-processing tasks implementations written in Java that can be used as a plugin for BDP4J framework. These task definitions implement efficient processing of the information exchanged through the different protocols and Internet services, using synsets and/or tokens. NLPA provides more than 30 pre-processing tasks applicable over corpora containing e-mails, websites, tweets, YouTube comments, SMS or plain text. The identification of these filetypes is made by using file extensions (.eml, .warc,.twtid, .ytbid, .tsms and .txt, respectively). Given the architecture of BDP4J framework, these pre-processing tasks are applied to org.bdp4j.types.Instance objects (see Figure 1).

![UML representation of BDP4J Instance class](https://moncho.mdez-reboredo.info/imgnlpa/Figure1.png)

*Figure 1. UML representation of BDP4J Instance class*

As shown in Figure 1, Instance class brings together for attributes: (i) source, (ii) name, (iii) data (iv) target and (v) props. In detail, source attribute stores the information required to access the source of the information compiled (usually a java.io.File is enough). Moreover, name stands for any form of unique identification of the instance. The processing of the instance implies a sequence of modifications of data attribute (originally with the same value as source attribute) while pre-processing tasks are applied. Logically, the modifications of data attribute could imply the loss of information from original data and the impossibility of returning to the original state of the data (that could be achieved from source attribute). Besides, target attribute is useful to address classification and prediction problems to include into the instance information about the real prediction/classification. Finally, props contains several properties that are being computed through pre-processing tasks (such as language, text-length, etc).
NLPA pre-processing tasks comprises five input types for data attribute: java.io.File, java.lang.StringBuffer, org.nlpa.types.SynsetSequence, org.nlpa.types.TokenSequence and org.nlpa.types.FeatureVector. Table 1 brings together the input and output data types of data attribute of all tasks included in our framework.

![Input and output data types for all tasks](https://moncho.mdez-reboredo.info/imgnlpa/Table1.png)

*Table 1. Input and output data types for all tasks*

As shown in Table 1, most tasks are designed to use *java.lang.StringBuffer* (a mutable representation of Strings) as input data. Some interesting operations such as the processing of abbreviations, slang, interjections, stop-words, URLs, references to users, etc. are made from this representation. Next subsections describe the tasks implemented by our framework classified by the data type required for data attribute of instances being processed.

<u>**Processing java.io.File data type**</u>

Although streaming sources could be easily used with NLPA, currently it uses locally stored files (i.e. in a local or network file system) represented through *java.io.File* objects as the main of reading data instances. Descriptions of each available task for processing instances containing *java.io.File* objects as data, are included below.

- *File2StringBufferPipe* is able to transform the data attribute of an instance from *java.io.File* to a *java.lang.StringBuffer* type. To this end, the textual content of the target file (only for supported formats) is stored in the data attribute.

- *GuessDateFromFilePipe* is able to find in input file (interpreting their formats) the date of the contents (when available). As result of this process, date is stored as a property of each instance (using “date” as default name).

- *StoreFileExtensionPipe* creates a property to insert the type of content into the instance properties (using “extension” as default name). The value of the property is computed as the extension of the file referenced by the data attribute of the instance.

- *TargetAssigningFromPathPipe* can be applied only for classification purposes. This task finds in the path of the File referenced by the data attribute of the instance if a folder matching the class is found. This task uses a transformation map to connect system folder names with instance categories.

Once a *java.io.File* is processed into a *java.lang.StringBuffer* (i.e. using File2StringBufferPipe task), a wide variety of operations can be used to pre-process text. Next subsection presents the tasks that can be used for processing *StringBuffer* object included as instance data.

<u>**Processing java.io.StringBuffer data type**</u>

*StringBuffer* class is used to represent and modify (issued by mutable property provided by *StringBuffer* Java class) textual contents. Once the text is extracted, a wide variety of tasks are provided to transform input contents dropping parts (interjections, stopwords, emoticons, etc.) and/or replacing text (slang forms, abbreviations, etc.). The descriptions of available tasks for processing this type of data (alphabetically ordered) are included below.

- *AbbreviationFromStringBufferPipe* detects abbreviations in text and expands them using dictionaries. Abbreviation dictionaries are implemented for several languages (such as English, Spanish, French or Russian) using JSON (JavaScript Object Notation) files. In order to properly select the right abbreviations dictionary, a property storing the language of text should previously exist (see *GuessLanguageFromStringBufferPipe*).

- *ComputePolarityFromStringBufferPipe* adds the polarity of the text as an instance property. Possible results are in the form of 5-level Likert scale (i.e. 0 to indicate "Very Negative", 1 to "Negative", 2 to "Neutral", 3 to "Positive" and 4 to "Very Positive"). The polarity is computed by taking advantage of Stanford NLP framework.

- *ComputePolarityTBWSFromStringBufferPipe* adds the polarity of the text computed by querying the TextBlob 1 Python library. The polarity score computed by using this library is a float within the range [-1.0, 1.0]. In order to query Python TextBlob library from Java, a REST (Representational State Transfer) web service (TBWS, TextBlob Web Service) has been developed and can be easily launched as a docker container using the scripts provided with the tool.

- *ContractionsFromStringBufferPipe* replaces contractions in the original text using dictionaries (JSON files). To made the replacements, some language-specific dictionary files are included in NLPA. In order to properly select the right contractions dictionary, a property storing the language of text should be previously computed (see *GuessLanguageFromStringBufferPipe*).

- *FindEmojiInStringBufferPipe* finds and removes (if desired) emojis from text and adds them as a property of the instance. By default, the property name is “emoji”. The process is made by taking advantage of emoji-java library 2.

- *FindEmoticonInStringBufferPipe* finds and removes (if needed) emoticons from text and creating a new property (named “emoticon” by default) for the instance. Emoticons are found through using a complex regular expression over the whole text. As the main limitations, *FindHashtagInStringBufferPipe* (see next paragraph) cannot be executed after this task.

- *FindHashtagInStringBufferPipe* searches for hashtags in text and adds them as a property (“hashtag” by default) of the instance. Additionally, the task can be configured to remove the identified hashtags from the original text. Internally, this task uses a regular expression to find hashtags in text.

- *FindUrlInStringBufferPipe* finds URLs from text adding them as a property (“URLs” by default) of the instance. Additionally, removing URLs from the original text is also possible. This task is made by using regular expressions. *FindUserNameInStringBufferPipe* (see next  paragraph) task cannot be executed after *FindUrlInStringBufferPipe*.

- *FindUserNameInStringBufferPipe* takes advantage of regular expressions to search and optionally remove tokens in the form “@<userName>” from text. Also, it adds the identified user references as a property of the instance (“@userName” by default). 

- *GuessLanguageFromStringBufferPipe* finds out the language of the text included in the instance. It adds “language” and “language-reliability” properties (by default) to the instance to store the result of the process. The data of the instance should contain text without HTML tags. To detect both the language and the probability of a successful identification, we take advantage of language-detector library for Java 3 , able to distinguish up to 71 languages.

- *InterjectionFromStringBufferPipe* allows to identify and optionally drop interjections from text using dictionaries (JSON files). It adds them into “interjection” property of the instance. As long as interjections are language-dependant, the language of the instance should be computed before executing it.

- *MeasureLengthFromStringBufferPipe* adds the “length” property (by default) computed by measuring the length of the text included in the data of the instance.

- *NERFromStringBufferPipe* implements NER adding all identified entities into instance properties and optionally deletes them from the input text. By default, date (property “NERDATE”), money (“NERMONEY”), number (“NERNUMBER”), address (“NERADDRESS”) and location (“NERLOCATION”) are the entities that can be recognized. NER is implemented through Stanford NLP framework.

- *SlangFromStringBufferPipe* detects slang terms in the input text and replaces by its formal term using dictionaries (JSON files). In order to select the appropriate dictionary, the language of the text should be previously computed.

- *StopWordFromStringBufferPipe* drops stopwords from text included in the data attribute of an instance. The language of text should be previously detected to select the appropriate stopword dictionary. Besides, *AbbreviationFromStringBufferPipe* task could not be executed after this one.

- *StringBufferToLowerCasePipe* transforms the textual content, included in the data attribute of an instance, to lowercase.

- Additionally, an instance containing a *StringBuffer* can be transformed into a *SynsetSequence* or a *TokenSequence*. These functionalities are implemented by *StringBuffer2SynsetSequencePipe* and *StringBuffer2TokenSequencePipe* respectively. The former takes advantage of Babelfy API 4 to recognize synsets of each word included in text. The second one implements a tokenizing process using a set of characters as word delimiters. 
- Furthermore, *TeeCSVFromStringBufferPipe* stores instances in a Comma Separated value(s) (CSV) file containing all computed properties together with the text of each them.

<u>**Processing org.nlp.types.SynsetSequence**</u>

A *SynsetSequence* object brings together a sequence of synsets that are found in the text of an instance. To handle instances with this data type as input, NLPA includes the task *SynsetSequence2FeatureVectorPipe*. It is able to transform a *SynsetSequence* into a *FeatureVector* which compiles together duplicated features and assigns a score for each feature according to a grouping scheme. The grouping scheme can be one of the following ones:

  * *SequenceGroupingStrategy.COUNT* that indicates the number of times that a synset is observed in the sequence
  * *SequenceGroupingStrategy.BOOLEAN* that assigns 1 when the synset is included in the content or 0 otherwise
  * *SequenceGroupingStrategy.FREQUENCY* that indicates the frequency of the synset in the text (number of times that the synset is observed divided by the whole amount of synsets)


<u>**Processing org.nlp.types.TokenSequence**</u>

A *TokenSequence* contains the sequence of tokens that are found in the text of an instance. To handle instances with this data type as input, NLPA includes the tasks described below.

- *TokenSequence2FeatureVectorPipe*, similarly to *SynsetSequence2FeatureVectorPipe*, transforms the list of tokens included in the text of the data instance into a *FeatureVector* according to a selected grouping scheme (*SequenceGroupingStrategy*).

- *TokenSequencePorterStemmerPipe* applies the Porter stemmer algorithm to the *TokenSequence* included in an instance. This scheme allows to reduce inflected (or sometimes derived) words to their stem form, using a set of language-dependant rules. As long as the rules are defined by language, it should be previously computed (see *GuessLanguageFromStringBufferPipe* in subsection 3.2). 

- *TokenSequenceStemIrregularPipe* applies irregular stemming (through language- dependant dictionaries) to tokens with the same purpose of the previous one. The irregular stemming task, if applied, should be executed before *TokenSequencePorterStemmerPipe* and the language of the text should be computed before its usage.

<u>**Processing org.nlp.types.FeatureVector**</u>

*FeatureVector* compiles together a set of features of text properties (synset-based or token-based) identified in the text of an instance and their values. To handle input instances with this data type, NLPA includes *TeeCSVFromFeatureVectorPipe* and *TeeDatasetFromFeatureVectorPipe* tasks. They allow to generate a dataset into disk (CSV format) or in memory (*org.bdp4j.types.Dataset*), respectively, for their further usage. These datasets can be easily used to execute experiments in Weka Machine Learning Software 5 through the functionalities provided by BDP4J framework.

Next section shows the usage of NLPA through a case study to show the creation and exploitation of a pipeline containing some of the previous described tasks.


## Using NLPA

NLPA is a plugin for BDP4J that implements a set of natural language processing task definitions. BDP4J is a pipelining Java framework able to combine and orchestrate the execution of pre-processing tasks in sequence or in parallel. The orchestration can be defined in Java source or using XML files. Additionally, BDP4J adds a wide variety of constraint checks that prevents development errors (dependencies and input/output types for tasks). BDP4J also supports the instance invalidation which is required when a NLPA task fails. Moreover, resuming the execution of pipelining processing after a hardware software failure. Finally, BDP4J supports the debugging mode that allows avoid the execution of some tasks by restoring the results achieved by them in a previous execution. This functionality was particularly useful during the development of NLPA plugin.

Figure 2 includes a class diagram and a snippet of source code showing the interaction of BDP4J and NLPA projects. Particularly, Figure 2a specifies some architecture details to facilitate the comprehension of the inner operation of both projects. As we can see from it, NLPA tasks (three of them have been included as example) are created as a subclass of org.bdp4j.pipe.AbstractPipe BDP4J.

![Interaction of BDP4J and NLPA projects](https://moncho.mdez-reboredo.info/imgnlpa/Figure2.png)

*Figure 2. Interaction of BDP4J and NLPA projects*


As we can see in Figure 1b, the pipeline orchestration comprises some tasks executed in sequence. In order to check whether the pipeline has been correctly defined, a dependency check is executed. Additionally, NLPA incorporates mechanisms to automatically load instances from files. And finally, instances are processed.


## License

Copyright (C) 2018 Sing Group (University of Vigo)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
