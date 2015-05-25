SkroutzGreekStemmer plugin for ElasticSearch
===========================================

This plugin is based on the GreekStemmer that is included in Apache Lucene.

Lucene's GreekStemmer is created according to _Development of a Stemmer for
the Greek Language_ of Georgios Ntaias. This thesis mentions that 166 suffixes
are recognized in the Greek language. However, only 158 were captured by this
stemmer, because the addition of the remainning suffixes would reduce the
precision of the stemmer on the word-sets that were used for its evaluation.

But the exclusion of these suffixes does not perform well on our word-set
which consists of more than __120.000__ words. So, for our needs we had to
modify the implementation of Lucene's GreekStemmer in order to include eight
more suffixes which improve the quality of our search results. Four of the
these new suffixes are not included to the 166 suffixes of the thesis of
Geogios Ntaias. These are:

    -ιο, ιοσ, -εασ, -εα

The remaining four suffixes are included in the set of the eight suffixes that
were intentionally not captured by the the original GreekStemmer. These
suffixes reflect different forms of the words that end with the first three of
the above suffixes and these are the following:

    -ιασ, -ιεσ, -ιοι, -ιουσ

Examples:

Word                 | GreekStemmer   | SkroutzGreekStemmer
---------------------|----------------|---------------------
κριτηριο (singular)  | κριτηρι        | κριτηρ
κριτηρια (plural)    | κριτηρ         | κριτηρ
προβολεας (singular) | προβολε        | προβολ
προβολεις (plural)   | προβολ         | προβολ
αμινοξυ (singular)   | αμινοξ         | αμινοξ
αμινοξεα (plural)    | αμινοξε        | αμινοξ

In order to install the latest version of the plugin, simply run:

    bin/plugin -install gr.skroutz/elasticsearch-skroutz-greekstemmer/0.0.6


In order to install versions prior to 0.0.2, simply run:

    bin/plugin -install skroutz/elasticsearch-skroutz-greekstemmer/0.0.1

Versions:

SkroutzGreekStemmer Plugin | ElasticSearch
---------------------------|------------------
0.0.6                      | 1.5.x
0.0.5                      | 0.90.3
0.0.4                      | 0.90.3
0.0.3                      | 0.90.2
0.0.2                      | 0.90.0
0.0.1                      | 0.19.4

Example usage
-------------

    # Create index
    $ curl -XPUT 'http://localhost:9200/test_stemmer' -d '{
      "settings":{
        "analysis":{
          "analyzer":{
            "stem_analyzer":{
              "type":"custom",
                "tokenizer":"standard",
                "filter": ["lower_greek", "stem_greek"]
            }
          },
          "filter": {
            "lower_greek": {
              "type":"lowercase",
              "language":"greek"
            },
            "stem_greek": {
              "type":"skroutz_stem_greek"
            }
          }
        }
      }
    }'
    {"acknowledged":true}

    # Test analyzer
    $ curl -XGET 'http://localhost:9200/test_stemmer/_analyze?analyzer=stem_analyzer&pretty=true' -d 'κουρευτικές μηχανές'
    {
      "tokens" : [ {
        "token" : "κουρευτ",
        "start_offset" : 0,
        "end_offset" : 11,
        "type" : "<ALPHANUM>",
        "position" : 1
      }, {
        "token" : "μηχαν",
        "start_offset" : 12,
        "end_offset" : 19,
        "type" : "<ALPHANUM>",
        "position" : 2
      } ]
    }

    $ curl -XGET 'http://localhost:9200/test_stemmer/_analyze?analyzer=stem_analyzer&pretty=true' -d 'κουρευτική μηχανή'
    {
      "tokens" : [ {
        "token" : "κουρευτ",
        "start_offset" : 0,
        "end_offset" : 10,
        "type" : "<ALPHANUM>",
        "position" : 1
      }, {
        "token" : "μηχαν",
        "start_offset" : 11,
        "end_offset" : 17,
        "type" : "<ALPHANUM>",
        "position" : 2
      } ]
    }

    # Delete test index
    $ curl -XDELETE 'http://localhost:9200/test_stemmer'
    {"ok":true,"acknowledged":true}

### YML configuration example

	index:
	  analysis:
	    filter:
	      stem_greek:
	        type: skroutz_stem_greek


Warning
-------

Input is expected to to be casefolded for Greek (including folding of final
sigma to sigma), and with diacritics removed. This can be achieved with
GreekLowerCaseFilter.

References
----------

* [Development of a Stemmer for the Greek Language](http://people.dsv.su.se/~hercules/papers/Ntais_greek_stemmer_thesis_final.pdf)


Issues
--------

For stemming issues: [here](https://github.com/skroutz/greek_stemmer)
