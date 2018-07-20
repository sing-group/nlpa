package org.ski4spam.util.dateextractor;

import java.io.File;
import java.util.Date;

public class TYTBDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private TYTBDateExtractor() {

    }

    public static String getExtension() {
        return "tytb";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TYTBDateExtractor();
        }
        return instance;
    }

    public Date extractDate(File f) {
		//May be: https://www.googleapis.com/youtube/v3/comments?part=snippet&id={COMMENT_ID}&textFormat=html&key={YOUR_API_KEY} 
		//Api_key: AIzaSyAgGPiyeKbC7xnY3eTmRXU22lxc2TpyQoE
		//CommentID example extracted from our test dataset: 1320068270000 
		//https://www.googleapis.com/youtube/v3/comments?part=snippet&id={COMMENT_ID}&textFormat=html&key=AIzaSyAgGPiyeKbC7xnY3eTmRXU22lxc2TpyQoE
		//https://www.googleapis.com/youtube/v3/comments?part=snippet&id=1320068270000&textFormat=html&key=AIzaSyAgGPiyeKbC7xnY3eTmRXU22lxc2TpyQoE
		
		//From this we achieve the following response
		//{
		// "kind": "youtube#commentListResponse",
		// "etag": "\"XI7nbFXulYBIpL0ayR_gDh3eu1k/pGLBhpjR05yQoJV31WoAx2PEFVw\"",
		// "items": []
		//}
		
		//However a reasonable response for this api could be the following (for a real comment id z12qxfxr2onpy1b5l04cdfzrgwabir0q4bo)
		//{
		// "kind": "youtube#commentListResponse",
		// "etag": "\"XI7nbFXulYBIpL0ayR_gDh3eu1k/QPwR7CfQifTCu8fIpXeN1JB6uVo\"",
		// "items": [
		//  {
		//   "kind": "youtube#comment",
		//   "etag": "\"XI7nbFXulYBIpL0ayR_gDh3eu1k/-Yfme6UEokAnV_-IrLs9i8FhvPU\"",
		//   "id": "UgixOYNaTF2qrXgCoAEC",
		//   "snippet": {
		//    "authorDisplayName": "Randy Taschner",
		//    "authorProfileImageUrl": "https://yt3.ggpht.com/--vE0X3_vDCs/AAAAAAAAAAI/AAAAAAAAAAA/P6kgycrPEZw/s28-c-k-no-mo-rj-c0xffffff/photo.jpg",
		//    "authorChannelUrl": "http://www.youtube.com/channel/UCTRuBHRb4BRFcob-hMj6NnQ",
		//    "authorChannelId": {
		//     "value": "UCTRuBHRb4BRFcob-hMj6NnQ"
		//    },
		//    "textDisplay": "Thank you Dan and Envato for creating this video!",
		//    "textOriginal": "Thank you Dan and Envato for creating this video!",
		//    "canRate": true,
		//    "viewerRating": "none",
		//    "likeCount": 1,
		//    "publishedAt": "2015-08-16T05:02:25.000Z",
		//    "updatedAt": "2015-08-16T05:02:25.000Z"
		//   }
		//  }
		// ]
		//}
		//
		//As can be seen the IDs included in the files does not fit yourtube comment IDs.

        return null;
    }
}