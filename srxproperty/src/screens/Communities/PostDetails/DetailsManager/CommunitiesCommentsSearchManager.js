import {CommunitiesService} from '../../../../services';
import {ObjectUtil} from '../../../../utils';
import {CommunityPostPO} from '../../../../dataObject';

class CommunitiesCommentsSearchManager {
  totalCounts = 0;
  commentResultArray = [];
  isLoading = false;
  startIndex = 0;

  constructor(data) {
    if (data) {
      const {postId} = data;
      this.options = {
        ...this.options,
        postId,
      };
    }
  }

  register = callBack => {
    this.callBack = callBack;
  };

  sendBackResponse = (startIndex, newComments, allComments, error) => {
    if (this.callBack) {
      this.callBack({
        startIndex,
        newComments,
        allComments,
        error,
        manager: this,
      });
    }
  };

  canLoadMore = (commentCount, commentTotal) => {
    return commentCount < commentTotal;
  };

  search = () => {
    if (this.isLoading) return;

    this.totalCounts = 0;
    this.commentResultArray = [];

    this.loadPostComments();
  };

  loadMore = commentCount => {
    if (this.isLoading) return;

    if (!this.canLoadMore(commentCount, this.totalCounts)) return;

    this.startIndex = commentCount;

    console.log('In loadMore startIndex' + this.startIndex);

    this.loadPostComments();
  };

  loadPostComments = () => {
    const {postId} = this.options;

    this.isLoading = true;
    CommunitiesService.getComments({
      postId: postId,
      startIndex: this.startIndex,
      maxResults: 20,
      orderBy: 'DESC',
    })
      .then(response => {
        console.log(response);
        console.log('Getting response in load post comments');

        const {comments, total} = response;
        const newComments = [];

        this.totalCounts = total ? total : 0;
        if (!ObjectUtil.isEmpty(comments)) {
          comments.map(item => {
            newComments.push(new CommunityPostPO(item));
          });

          if (!ObjectUtil.isEmpty(newComments)) {
            this.commentResultArray = [
              ...this.commentResultArray,
              ...newComments,
            ];
          }
        } //end of comments

        this.isLoading = false;
        this.sendBackResponse(
          this.startIndex,
          newComments,
          this.commentResultArray,
          null,
        );
      })
      .catch(error => {
        this.isLoading = false;
        this.sendBackResponse(this.startIndex, null, null, error);
      });
  };
}

export {CommunitiesCommentsSearchManager};
