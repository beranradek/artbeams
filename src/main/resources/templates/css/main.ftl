<#macro main>
/* Globals */
.bd-placeholder-img {
  font-size: 1.125rem;
  text-anchor: middle;
}

@media (min-width: 768px) {
  .bd-placeholder-img-lg {
    font-size: 3.5rem;
  }
}

.row {
  /* Fixed -15px margins that injures mobile design */
  margin-right: 0rem;
  margin-left: 0rem;
}

.page-content {
  margin-top: 1rem;
}

main > .container {
  padding: 60px 0px 0;
}

.footer {
  background-color: #f5f5f5;
  font-size: 0.95rem;
}

.footer > .container {
  padding-right: 15px;
  padding-left: 15px;
}

/* Layout customization */
.bg-dark {
    background-color: #86b300!important;
}

.header {
}

.navbar {
    background-color: #f5f5f5;
}

a.navbar-brand {
    color: green;
    font-weight: 600;
}

.navbar-dark .navbar-nav .nav-link {
    color: rgb(242, 242, 242);
}

.nav-item {
    border-left: 1px dotted #68adf7;
    padding-left: 0.5rem;
    padding-right: 0.5rem;
}

/* Custom styles */
.navbar a, .navbar a:active, .navbar a:hover, .navbar a:visited {
    color: #2b5989;
}

.item-title a, .item-title a:active, .item-title a:hover, .item-title a:visited {
    color: Chocolate;
}

a.navbar-brand, a:active.navbar-brand, a:visited.navbar-brand {
    /* color: DarkGreen */
    color: #2b5989;
}

.btn {
    color: white;
    background-color: Chocolate !important;
}

.btn-search {
    background-color: #2b5989 !important;
}

#headline {
    background-position: center center;
    background-repeat: no-repeat;
    -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;
    background-size: cover;
    /* height: auto; */
    /* background-attachment: fixed: Stays in fixed position when scrolling (quite nice effect). */
    height: ${xlat['headline.img.height']}px;
    width: 100%;
    margin-bottom: 2rem;
    border-bottom: 0px solid;
    border-top: 0px solid;
}

#headline-offer {
    z-index: 1;
    background-color: rgba(0, 0, 0, 0.23);
    color: white;
    border-radius: 1rem;
    padding: 2rem;
    margin: 1rem 0rem 0rem 0rem;
    min-width: 360px; /* min-width so the column does not become too narrow on tablets  */
}

#headline-portrait {
    display: inline-block;
    width: 100%;
    height: 100%;
    vertical-align: middle;
    text-align: center;
}

#headline-portrait-holder {
    display: inline-block;
    margin-top: 30%;
    border: solid 3px #0f283d;
    background-color: #192d3d;
    border-radius: 0.8rem;
}

.category-header {
    text-align: center;
    background-color: Chocolate;
    padding-top: 0.8rem;
    padding-bottom: 0.8rem;
}
.category-header h1 {
  color: white;
  font-size: 1.8rem;
}

.cookie-info-bar {
    position: fixed;
    background: rgba(0,0,0,0.9);
    padding: 8px 12px;
    color: #ddd;
    font-size: 13px;
    line-height: 1.2em;
    width: 100%;
    bottom: 0;
    left: 0;
    z-index: 999999999;
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
}

.cookie-info-button {
    margin-left: 1rem;
}

.author {
    border-top: 1px solid lightgrey;
    margin-top: 1rem;
    margin-bottom: 1rem;
}

.metadata {
    font-size: 90%;
    color: #bbb;
    border-bottom: 1px solid lightgrey;
    margin-top: 1rem;
    padding-bottom: 1rem;
    margin-bottom: 1rem;
}
.metadata a {
    color: #ccc;
}

.avatar {
    margin-right: 1rem;
}

.comments {
    border-top: 1px solid lightgrey;
    padding-top: 1rem;
    padding-bottom: 1rem;
    margin-bottom: 1.5rem;
}

.comments .comment {
    border-bottom: 1px solid lightgrey;
    padding: 0.5rem 0 0 0;
    margin: 1rem 0 0 0;
}

.comments .comment .comment-author {
    float: left;
    font-size: smaller;
}

.comments .comment .comment-meta {
    float: right;
    font-size: smaller;
}

.comments .comment .comment-body {
    clear: both;
}

.comment-info {
    font-size: 0.95rem;
}

body {
  font-size: 1rem;
  line-height: 1.5rem;
  color: black;
  background-color: #fff;
}

.blog-main {
  font-size: 1.05rem;
  line-height: 1.6rem;
}

.item-date {
  font-size: 0.8rem;
  margin: 0.2rem;
}

.blog-item {
  margin-bottom: 25px;
  float: left;
}

.blog-item-image {
  float: left;
  box-sizing: border-box;
  padding-left: 20px;
  padding-top: 10px;
  padding-bottom: 10px;
  min-width: 260px;
  display: block;
}

.blog-item-text {
  float: right;
  box-sizing: border-box;
  padding-left: 20px;
  padding-right: 10px;
  padding-top: 10px;
  padding-bottom: 10px;
  display: block;
}

.share-buttons {
  text-align: center;
  padding-top: 0.5rem;
  padding-bottom: 1rem;
}

.article-image-detail {
  margin-top: 1.5rem;
  text-align: center;
}

</#macro>
