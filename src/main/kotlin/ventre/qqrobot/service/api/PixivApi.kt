package ventre.qqrobot.service.api

import retrofit2.http.*


data class PixivResponse(
    val error: Boolean = false,
    val body: PixivImageSearchResult? = null
)

data class PixivImageSearchResult(
    val illust: PixivIllust = PixivIllust()
)

data class PixivIllust(
    val total: Long = 0L,
    val data: List<PixivImage> = emptyList()
)

data class PixivImage(
    val id: String = "",
    val title: String = "",
    val url: String = "",
    val pageCount: Int = 1
)

interface PixivApi {
    companion object {
        const val baseUrl = "https://www.pixiv.net/"
    }

    /**
     * 查询图片
     */
    @Headers(
        *arrayOf("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
            "cookie: first_visit_datetime_pc=2023-01-20+09%3A18%3A38; p_ab_id=7; p_ab_id_2=4; p_ab_d_id=66051618; yuid_b=QXIRBpY; __utmc=235335808; __utmz=235335808.1674173919.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); PHPSESSID=21819002_am353fLOaTdyNgdSRF3XtX5aYje2UkIA; device_token=78fa7a619a15616cc60ce6ccd05c2dba; c_type=32; privacy_policy_notification=0; a_type=0; b_type=1; __utmv=235335808.|2=login%20ever=no=1^3=plan=normal=1^5=gender=male=1^6=user_id=21819002=1^9=p_ab_id=7=1^10=p_ab_id_2=4=1^11=lang=zh=1; privacy_policy_agreement=5; _im_vid=01GQQQDFTDC44QWVQZCX652KNT; cto_bundle=pUHT819QNkYlMkJrTTRNczluMzZjYSUyRlE4JTJCJTJCdVQ0S3N2QWU5MEZPUHFRR3NJMEdLJTJCRTFhVkRNMlVNeVAlMkZtcjhnNE9SRUVtdUNoUGdLUyUyRjN4M0JhMGd1bUNJJTJCQ0JUbEZLN2I4RHY1NFFNczBSdXQ5d255SEx4a1hYaDlyaEhDMzhJRGVYaHBPTFg2aFJUaDNaanladHpXV2NVbUpBJTNEJTNE; _ga_MZ1NL4PHH0=GS1.1.1674810118.2.0.1674810122.0.0.0; _gcl_au=1.1.1288385910.1675253392; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; _gid=GA1.2.787690426.1675443524; howto_recent_view_history=11440723%2C79405396%2C85467874; __utma=235335808.1491413864.1674173919.1675610261.1675697003.26; __cf_bm=B2ev.UzL5cQXTIHZpwJ8rGa00NhUop_UgIyum5ZEESQ-1675697002-0-AQr64LPPH4cvVz25Kzn5elV08SSc7HtGTURGXPitY1Q4t+KFmL09wPb0WwDsKRPWvmU/8pCAHBoP/xhsDv5kFN7pcATe2oJdM9FTj2jfWjUhCng7R34rVitXQj78XEGblWFGFqOuvLdCO7mtOjZdb62tybF2RIhqdR2JShagPso3eNculsc9APOVN2XL8eXzRYhgN5ViiKo4/aIbkU1MBkA=; _ga=GA1.2.1491413864.1674173919; __utmb=235335808.4.10.1675697003; tag_view_ranking=0xsDLqCEW6~3gjIvNWGCE~k3AcsamkCa~azESOjmQSV~ay54Q_G6oX~qtVr8SCFs5~RTJMXD26Ak~NT6HjMvlFJ~tgP8r-gOe_~3cT9FM3R6t~aKhT3n4RHZ~BN7FnIP06x~bhGHO52dlK~Bd2L9ZBE8q~engSCj5XFq~0unkWsk4kG~zZZn32I7eS~Lt-oEicbBr~q3eUobDMJW~IEelj7HCPz~q303ip6Ui5~oJAJo4VO5E~VN7cgWyMmg~abNIEh2zTB~u70hmuJXYF~GNcgbuT3T-~QL2G1t5h_V~6293srEnwa~edF4CoWy9T~nf2nMUUZFU~faHcYIP1U0~_EOd7bsGyl~NHNBwIWrH_~Cm1Eidma50~c15D8Cg2xk~59dAqNEUGJ~qWFESUmfEs~VOCbo66k-E~kmjQstF2cF~djjiDxPp_t~4QveACRzn3~KN7uxuR89w~MnGbHeuS94~ziiAzr_h04~Xyw8zvsyR4~Txs9grkeRc~yTfty1xk17~o7hvUrSGDN~sqGkVxMuMR~vkjGL_ISan~NsbQEogeyL~WVrsHleeCL~kngi6Qb_Ct~RcahSSzeRf~qkC-JF_MXY~kfohPZSK7g~NJN_Sd1EhI~zyKU3Q5L4C~2kSJBy_FeR~2acjSVohem~GX5cZxE2GY~Ngz9KxUrJt~b_G3UDfpN0~yZf1XmIy-U~lQGtQGMEhM~j0ZJDxtRJJ~sKSC2tjEOi~HoP7awChmC~cdylk7gwVV~Ph8TwL6RIX~YUuqn7At7n~Qv-rNOx_aa~i4xDq8M0zV~RybylJRnhJ~2GVngxkxTb~J9DAOECDjo~6a53MEkYjr~geHNPfE2P2~ncZciR8mCC~Ob2dVjBvWQ~liM64qjhwQ~-WrwnYvTU5~OAabTrZOMl~QX5HBa7hQV~i_Tmrd1yqc~walSzW1oI5~3mrRzVIdse~9-mAp6cGRA~HBlflqJjBZ~lPWnqPImPM~L-vuOMpFuE~JXmGXDx4tL~K8esoIs2eW~y_PNDq1bu3~jObBXjrg2v~gVfGX_rH_Y~G97YKD2Gc7~_AKBg0O8RH~5f1R8PG9ra~L58xyNakWW; _ga_75BBYNYN9J=GS1.1.1675697002.34.1.1675697179.0.0.0")
    )
    @GET("ajax/search/illustrations/{word}")
    suspend fun search(
        @Path("word") keyword: String = "",
        @Query("p") page: Int,
        @Query("mode") mode: String = "safe", //关闭r18
        @Query("s_mode") searchMode: String = "s_tag",
        @Query("order") order: String = "popular_d",
    ): PixivResponse
}