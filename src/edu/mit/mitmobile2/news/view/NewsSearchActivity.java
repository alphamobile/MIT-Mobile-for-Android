package edu.mit.mitmobile2.news.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SearchActivity;
import edu.mit.mitmobile2.news.net.NewsDownloader;
import edu.mit.mitmobile2.news.net.StoriesProgressListener;
//import edu.mit.mitmobile2.news.net.NewsDownloader.DownloadStoriesTask;
import edu.mit.mitmobile2.news.view.NewsArrayAdapter;
import edu.mit.mitmobile2.news.NewsModule;
//import edu.mit.mitmobile2.news.NewsModel;
//import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.news.NewsSearchSuggestionsProvider;
import edu.mit.mitmobile2.news.beans.NewsStory;
//import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.SearchResults;

public class NewsSearchActivity extends SearchActivity<NewsStory> {

	//protected NewsModel mNewsModel;
	private String mSearchTerm;
	private NewsDownloader nd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		nd = NewsDownloader.getInstance(this);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected ArrayAdapter<NewsStory> getListAdapter(final SearchResults<NewsStory> results) {
		List<NewsStory> newsItems = results.getResultsList();		
		return new NewsArrayAdapter(this, 0, newsItems);
	}

	@Override
	protected String getSuggestionsAuthority() {
		return NewsSearchSuggestionsProvider.AUTHORITY;
	}

	@Override
	protected void initiateSearch(String searchTerm, final Handler uiHandler) {
		mSearchTerm = searchTerm;
		NewsDownloader.DownloadStoriesTask dst = nd.new DownloadStoriesTask(new StoriesProgressListener(){
			//ArrayList<NewsStory> allStories = new ArrayList<NewsStory>();
			SearchResults<NewsStory> results;
			@Override
			public void onProgressUpdate(ArrayList<NewsStory>... list) {
				for(ArrayList<NewsStory> st:list){
					if(st==null || st.size()<1)
						continue;
					if(results == null)
						results = new SearchResults<NewsStory>(mSearchTerm,st);
					else
						results.addMoreResults(st);
				}
				
			}
			@Override
			public void onPostExecute(Long nr) {
				MobileWebApi.sendSuccessMessage(uiHandler, results);
			}
		}, "search");
		dst.execute(new String[]{mSearchTerm});
	}

	@Override
	protected String searchItemPlural() {
		return "stories";
	}

	@Override
	protected String searchItemSingular() {
		return "story";
	}

    @Override
    protected boolean supportsMoreResult() {
        return true;
    }

	@Override
	protected void onItemSelected(SearchResults<NewsStory> results, NewsStory item) {
		Intent intent = new Intent(NewsSearchActivity.this, NewsDetailsActivity.class);
		intent.putExtra(NewsDetailsActivity.KEY_POSITION, results.getItemPosition(item));
		intent.putExtra(NewsDetailsActivity.SEARCH_TERM_KEY, results.getSearchTerm());
		startActivity(intent);
		
	}

	@Override
	protected NewModule getNewModule() {
		return new NewsModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void continueSearch(SearchResults<NewsStory> previousResults, final Handler uiHandler) {
		NewsDownloader.DownloadStoriesTask dst = nd.new DownloadStoriesTask(new StoriesProgressListener(){
			SearchResults<NewsStory> results;
			@Override
			public void onProgressUpdate(ArrayList<NewsStory>... list) {
				for(ArrayList<NewsStory> st:list){
					if(st==null || st.size()<1)
						continue;
					if(results==null)
						results = new SearchResults<NewsStory>(mSearchTerm,st);
					else
						results.addMoreResults(st);
				}
				
			}
			@Override
			public void onPostExecute(Long nr) {
				MobileWebApi.sendSuccessMessage(uiHandler, results);
			}
			
		}, "search",previousResults.getResultsList().size(),20);
		dst.execute(new String[]{mSearchTerm});
		//mNewsModel.executeSearch(mSearchTerm, uiHandler, previousResults.getResultsList().size());
		
	}
}