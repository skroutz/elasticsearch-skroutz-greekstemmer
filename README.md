SkroutzGreekStemmer plugin for ElasticSearch
===========================================

This plugin is based on the GreekStemmer that is included in Apache Lucene.

Lucene's GreekStemmer is created according to _Development of a Stemmer for the Greek Language_ of Georgios Ntaias. This thesis mentions that 166 suffixes are recognized in the Greek language. However, only 158 were captured by this stemmer, because the addition of the remainning suffixes would reduce the precision of the stemmer on the word-sets that were used for its evaluation.

However, the exclusion of these suffixes does not perform well on our word-set which consists of more than 120.000 words. So, for our needs we had to modify the implementation of Lucene's GreekStemmer in order to include eight more suffixes which improve the quality of our search results. Four of the these new suffixes are not included to the 166 suffixes of the thesis of Geogios Ntaias. These are:

    -ιο, ιοσ, -εασ, -εα

The remaining four suffixes are included in the set of the eight suffixes that were intentionally not captured by the the original GreekStemmer. These suffixes reflect different forms of the words that end with the first three of the above suffixes and these are the following:

    -ιασ, -ιεσ, -ιοι, -ιουσ

Examples:

    ---------------------------------------------------------------
    | Word                 | GreekStemmer   | SkroutzGreekStemmer |
    ---------------------------------------------------------------
    | κριτηριο (singular)  | κριτηρι        | κριτηρ              |
    ---------------------------------------------------------------
    | κριτηρια (plural)    | κριτηρ         | κριτηρ              |
    ---------------------------------------------------------------
    | προβολεας (singular) | προβολε        | προβολ              |
    ---------------------------------------------------------------
    | προβολεις (plural)   | προβολ         | προβολ              |
    ---------------------------------------------------------------
    | αμινοξυ (singular)   | αμινοξ         | αμινοξ              |
    ---------------------------------------------------------------
    | αμινοξεα (plural)    | αμινοξε        | αμινοξ              |
    ---------------------------------------------------------------


In order to install the plugin, simply run: `bin/plugin -install skroutz/elasticsearch-skroutz-greekstemmer/0.0.1`.

    ----------------------------------------------
    | SkroutzGreekStemmer Plugin | ElasticSearch |
    ----------------------------------------------
    | 0.0.1                      | 0.19.4        |
    ----------------------------------------------



Example usage:

	index:
	  analysis:
	    filter:
	      stem_greek:
	        type: skroutz_stem_greek


Warning
-------

Input is expected to to be casefolded for Greek (including folding of final sigma to sigma), and with diacritics removed. This can be achieved with GreekLowerCaseFilter.
