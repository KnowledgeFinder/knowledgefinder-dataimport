# 2.0.0

## 2018-07-06 

### changes

* adapted to work with solr 7.2.0
* all solr configuration files are moved to the configuration projects
* maven builds are now generated in the configuration projects

### bugfix

* remove the last references to the projects working title "kownledgefinderII"

# 1.0.0

## 2017-03-10

### new (initial release)

* SVN data store crawler and parser
* Category parser
* Transformer
	* ArrayToStringTransformer
	* AuthorFormatingTransformer
	* CategoriesSeparatedTransformer
	* DateIncompleteFormatTransformer
	* DateToStringTransformer
	* DictToStringTransformer
	* ExcludeValuesTransformer
	* FilePathTransformer
	* FormatingStringTransformer
	* IntToDateTransformer
	* SelectLatestDateTransformer
	* TrimTransformer
	* URLDecodeTransformer
