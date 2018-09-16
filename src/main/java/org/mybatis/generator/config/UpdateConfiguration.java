package org.mybatis.generator.config;

import java.util.List;

/**
 * @author YangXiangTian
 */
public class UpdateConfiguration {

    /**
     * 候选码组
     */
    private List<CandidateKey> candidateKeys;

    public List<CandidateKey> getCandidateKeys() {
        return candidateKeys;
    }

    public void setCandidateKeys(List<CandidateKey> candidateKeys) {
        this.candidateKeys = candidateKeys;
    }
}
