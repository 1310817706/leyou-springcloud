package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;


    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid){

        List<SpecGroup>   groups=this.specificationService.queryGroupsByCid(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);


    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required = false)Boolean searching
            ){

        List<SpecParam> params=this.specificationService.queryParams(gid,cid,generic,searching);

        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(params);


    }


    @GetMapping("group/param/{cid}")
    public  ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups=this.specificationService.queryGroupsByWithParam(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }


}
