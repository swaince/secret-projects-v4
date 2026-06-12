package com.dfec.soft.secret.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.dto.request.PostPageRequest;
import com.dfec.soft.secret.system.entity.SysPost;
import com.dfec.soft.secret.system.mapper.PostMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 岗位服务测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class PostServiceImplTest {

    private final PostService postService;
    private final PostMapper postMapper;

    PostServiceImplTest(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @BeforeEach
    void setUp() {
        postMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldPagePosts() {
        createPost("岗位A", "code_a", 1);
        createPost("岗位B", "code_b", 2);

        PostPageRequest request = new PostPageRequest();
        request.setPage(1L);
        request.setSize(10L);

        PageResponse<PostDTO> response = postService.page(request);

        assertThat(response.getRecords()).hasSize(2);
        assertThat(response.getTotal()).isEqualTo(2L);
        assertThat(response.getPage()).isEqualTo(1L);
        assertThat(response.getSize()).isEqualTo(10L);
    }

    @Test
    void shouldCreatePost() {
        PostDTO request = new PostDTO();
        request.setPostName("开发员");
        request.setPostCode("developer");
        request.setPostLevel(3);
        request.setSortOrder(3);
        request.setRemark("开发岗位");

        PostDTO result = postService.create(request, "test-user");

        assertThat(result.getPostId()).isNotNull();
        assertThat(result.getPostName()).isEqualTo("开发员");
        assertThat(result.getPostCode()).isEqualTo("developer");
        assertThat(postMapper.selectById(result.getPostId())).isNotNull();
    }

    @Test
    void shouldRejectDuplicatePostCode() {
        createPost("岗位A", "dup_code", 1);

        PostDTO request = new PostDTO();
        request.setPostName("岗位B");
        request.setPostCode("dup_code");

        assertThatThrownBy(() -> postService.create(request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("岗位编码已存在");
    }

    @Test
    void shouldUpdatePost() {
        String postId = createPost("原名称", "update_code", 1);

        PostDTO request = new PostDTO();
        request.setPostName("新名称");
        request.setPostCode("update_code");
        request.setPostLevel(2);

        PostDTO result = postService.update(postId, request, "test-user");

        assertThat(result.getPostName()).isEqualTo("新名称");
        assertThat(result.getPostLevel()).isEqualTo(2);
    }

    @Test
    void shouldRejectUpdateBuiltinPost() {
        String postId = createBuiltinPost("内置岗位", "builtin_code", 1);

        PostDTO request = new PostDTO();
        request.setPostName("修改名称");
        request.setPostCode("builtin_code");

        assertThatThrownBy(() -> postService.update(postId, request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置岗位不可修改");
    }

    @Test
    void shouldDeletePost() {
        String postId = createPost("待删除", "delete_code", 1);

        String deletedId = postService.deleteById(postId, "test-user");

        assertThat(deletedId).isEqualTo(postId);
        assertThat(postMapper.selectById(postId)).isNull();
    }

    @Test
    void shouldRejectDeleteBuiltinPost() {
        String postId = createBuiltinPost("内置岗位", "builtin_del", 1);

        assertThatThrownBy(() -> postService.deleteById(postId, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("内置岗位不可删除");
    }

    @Test
    void shouldBatchDeleteSkipBuiltin() {
        String normalId = createPost("普通岗位", "normal_code", 1);
        String builtinId = createBuiltinPost("内置岗位", "builtin_batch", 1);
        List<String> ids = Arrays.asList(normalId, builtinId);

        List<String> result = postService.delete(ids, "test-user");

        assertThat(result).containsExactly(normalId);
        assertThat(postMapper.selectById(normalId)).isNull();
        assertThat(postMapper.selectById(builtinId)).isNotNull();
    }

    private String createPost(String postName, String postCode, int postLevel) {
        PostDTO request = new PostDTO();
        request.setPostName(postName);
        request.setPostCode(postCode);
        request.setPostLevel(postLevel);
        return postService.create(request, "test-user").getPostId();
    }

    private String createBuiltinPost(String postName, String postCode, int postLevel) {
        PostDTO request = new PostDTO();
        request.setPostName(postName);
        request.setPostCode(postCode);
        request.setPostLevel(postLevel);
        String postId = postService.create(request, "test-user").getPostId();
        SysPost entity = postMapper.selectById(postId);
        entity.setBuiltIn(1);
        postMapper.updateById(entity);
        return postId;
    }
}
