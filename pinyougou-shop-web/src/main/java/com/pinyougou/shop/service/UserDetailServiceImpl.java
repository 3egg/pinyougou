package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

//实现了spring-security这个接口
public class UserDetailServiceImpl implements UserDetailsService {

    //相当于@Reference
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("passed UserServiceImpl");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        TbSeller one = sellerService.findOne(username);
        if(one != null){//有这个对象
            if("1".equals(one.getStatus())){//状态为已审核
                return new User(username, one.getPassword(), grantedAuthorities);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
