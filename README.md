# PopularMovies

## res/strings.xml
You have to replace your [TMDb](https://www.themoviedb.org/) API key as follows:

```
    <string name="api_key">api_key_here</string>
```


## left to do
My implementation of parcelable isn't perfect.
When you switch portrait/horizontal mode, current contents are kept the same, but newly downloaded contents follows below.
It makes incorrect ordering.
