package com.equalexperts.client.softwaredesignsystems.eatout.services

class RemoteLocationSearchService : LocationSearchService{
    override fun search(query: String, response: (LocationSearchResult) -> Unit) {
        response(LocationSearchResult.NotFound)
    }
}
