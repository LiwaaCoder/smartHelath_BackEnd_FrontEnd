package superapp.logic.Mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import superapp.Boundary.*;
import superapp.Boundary.User.UserId;
import superapp.dal.MiniAppCommandRepository;
import superapp.data.mainEntity.MiniAppCommandEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.logic.service.MiniAppCommandService;
import superapp.logic.utilitys.GeneralUtility;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class MiniAppCommandServiceRepo implements MiniAppCommandService {

    private  String springApplicationName;
    private final MiniAppCommandRepository repository;
    private final MongoTemplate mongoTemplate;

    // this method injects a configuration value of spring
    @Value("${spring.application.name:iAmTheDefaultNameOfTheApplication}")
    public void setSpringApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
    }

    @PostConstruct
    public void init() {
        if (!mongoTemplate.collectionExists("COMMAND")) {
            mongoTemplate.createCollection("COMMAND");
        }
    }

    @Autowired
    public MiniAppCommandServiceRepo(MongoTemplate mongoTemplate,
                                     MiniAppCommandRepository repository) {

        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MiniAppCommandBoundary invokeCommand(MiniAppCommandBoundary miniAppCommandBoundary) throws RuntimeException {
        try {
            validatMiniappCommand(miniAppCommandBoundary);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

        CommandId commandId = miniAppCommandBoundary.getCommandId();
        if (commandId == null || commandId.getInternalCommandId() == null) {
            // Handle the case where CommandId or InternalCommandId is not provided by the client
            // You can choose to generate a default CommandId or handle it in a different way
            // Here, we are setting default values for CommandId
            commandId = new CommandId(springApplicationName, "default-miniapp", UUID.randomUUID().toString());
            miniAppCommandBoundary.setCommandId(commandId);
        } else if (commandId.getSuperapp() == null) {
            // Handle the case where Superapp is not provided by the client
            // You can set a default value or handle it in a different way based on your requirements
            commandId.setSuperapp(springApplicationName);
        }

        miniAppCommandBoundary.setInvocationTimestamp(new Date());

        MiniAppCommandEntity entity = boundaryToEntity(miniAppCommandBoundary);
        entity = this.repository.save(entity);
        return this.entityToBoundary(entity);
    }


    private void validatMiniappCommand(MiniAppCommandBoundary miniAppCommandBoundary) throws RuntimeException {
        GeneralUtility generalUtility = new GeneralUtility();
        if (miniAppCommandBoundary.getCommandAttributes() == null) {
            throw new RuntimeException("Command attributes are missing");
        }
        if (generalUtility.isStringEmptyOrNull(miniAppCommandBoundary.getCommand())) {
            throw new RuntimeException("Command details are missing");
        }
        if (generalUtility.isStringEmptyOrNull(miniAppCommandBoundary.getInvokedBy().getUserId().getEmail()) ||
        generalUtility.isStringEmptyOrNull(miniAppCommandBoundary.getInvokedBy().getUserId().getSuperapp())) {
            throw new RuntimeException("Invoked by is missing");
        }
        if (generalUtility.isStringEmptyOrNull(miniAppCommandBoundary.getTargetObject().getObjectId().getInternalObjectId()) ||
                generalUtility.isStringEmptyOrNull(miniAppCommandBoundary.getTargetObject().getObjectId().getSuperapp())) {
            throw new RuntimeException("Target object is missing");
        }
    }


    @Override
    public void deleteAllCommands() throws RuntimeException {
        try {
            this.repository.deleteAll();
        }catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MiniAppCommandBoundary> getAllCommands() throws RuntimeException {
        List<MiniAppCommandEntity> entities = this.repository.findAll();
        if (entities.isEmpty()){
            throw new RuntimeException("there aren't any commands");
        }
        List<MiniAppCommandBoundary> boundaries = new ArrayList<>();
        for (MiniAppCommandEntity entity : entities) {
            boundaries.add(this.entityToBoundary(entity));
        }
        return boundaries;
    }

    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) throws RuntimeException {
        List<MiniAppCommandEntity> entities = repository.findAllByCommandIdMiniapp(miniAppName);
        if (entities.isEmpty()){
            throw new RuntimeException("mini app history is empty");
        }
        return entities.stream()
                .map(this::entityToBoundary)
                .collect(Collectors.toList());
    }



    public MiniAppCommandBoundary entityToBoundary(MiniAppCommandEntity entity) {
        MiniAppCommandBoundary boundary = new MiniAppCommandBoundary();

        boundary.setCommandId(entity.getCommandId());
        boundary.setCommandAttributes(entity.getCommandAttributes());
        boundary.setCommand(entity.getCommand());
        boundary.setTargetObject(entity.getTargetObject());
        boundary.setInvocationTimestamp(entity.getInvocationTimestamp());
        boundary.setInvokedBy(entity.getInvokedBy());

        return boundary;
    }


    public  MiniAppCommandEntity boundaryToEntity(MiniAppCommandBoundary obj)
    {
        MiniAppCommandEntity entity = new MiniAppCommandEntity();

        entity.setCommand(obj.getCommand());
        if (obj.getCommandId() == null) {
            entity.setCommandId(new CommandId());
        }
        else
            entity.setCommandId(obj.getCommandId());

        if (obj.getCommand() == null) {
            entity.setCommand("");
        }
        else
            entity.setCommand(obj.getCommand());


        if (obj.getTargetObject() == null) {
            entity.setTargetObject(new TargetObject(new ObjectId()));
        }
        else
            entity.setTargetObject(obj.getTargetObject());


        if (obj.getInvokedBy() == null) {
            entity.setInvokedBy(new InvokedBy(new UserId()));
        }else {
            entity.setInvokedBy(obj.getInvokedBy());
        }
        if (obj.getCommandAttributes() == null){
            entity.setCommandAttributes(new HashMap<>() {
            });
        }else {
            entity.setCommandAttributes(obj.getCommandAttributes());
        }

        //Date
        entity.setInvocationTimestamp(obj.getInvocationTimestamp());

        //Data
        entity.setCommandAttributes(obj.getCommandAttributes());

        return entity;
    }
}
