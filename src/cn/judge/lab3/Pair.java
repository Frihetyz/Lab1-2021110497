package cn.judge.lab3;

/**
 * Pair类表示一个有序对.
 *
 * @param <T> 第一个元素的类型.
 * @param <U> 第二个元素的类型.
 */
class Pair<T, U> {
    /**
     * 第一个元素.
     */
    private final T firstElement;

    /**
     * 第二个元素.
     */
    private final U secondElement;

    /**
     * 构造一个带有给定元素的新Pair.
     *
     * @param firstEle 第一个元素.
     * @param secondEle 第二个元素.
     */
    Pair(final T firstEle, final U secondEle) {
        this.firstElement = firstEle;
        this.secondElement = secondEle;
    }

    /**
     * 获取第一个元素.
     *
     * @return 第一个元素.
     */
    public T getFirst() {
        return firstElement;
    }

    /**
     * 获取第二个元素.
     *
     * @return 第二个元素.
     */
    public U getSecond() {
        return secondElement;
    }
}
