package cn.garymb.ygomobile.core.loader;

public interface ICardLoader{
    void search(String prefixWord, String suffixWord,
                long attribute, long level, long race,long limitlist,long limit,
                String atk, String def,
                long setcode, long category, long ot, long... types);

    void onReset();
}